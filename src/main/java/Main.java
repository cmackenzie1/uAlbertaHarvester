/*
 *    Copyright 2019 Cole Mackenzie
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {
    private static final String LDAP_HOST = "directory.srv.ualberta.ca";
    private static final String ROOT_DN = "ou=calendar,dc=ualberta,dc=ca";
    private static final String TERM_QS = "ou=calendar,dc=ualberta,dc=ca";
    private static final String TERM_FILTER = "(objectclass=uOfATerm)";
    private static final String COURSE_QS = "term=%s,ou=calendar,dc=ualberta,dc=ca";
    private static final String COURSE_FILTER = "(objectclass=uOfACourse)";
    private static final String CLASS_QS = "term=%s,ou=calendar,dc=ualberta,dc=ca";
    private static final String CLASS_FILTER = "(objectClass=uOfAClass)";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int SIZE_LIMIT = 10000;

    public static void main(String[] args) {
        ArrayNode terms;
        ArrayNode courses;

        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost(LDAP_HOST);
        config.setLdapPort(389);
        DefaultLdapConnectionFactory factory = new DefaultLdapConnectionFactory(config);
        factory.setTimeOut(3000);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        LdapConnectionPool ldapConnectionPool = new LdapConnectionPool(
                new DefaultPoolableLdapConnectionFactory(factory), poolConfig);

        try {
            // Terms
            terms = query(ldapConnectionPool, TERM_QS, TERM_FILTER, SearchScope.ONELEVEL);
            log.info("Collected {} terms", terms.size());
            writeToJson(Paths.get("uAlbertaHarvesterDump/terms.json"), terms);

            // Courses
            courses = objectMapper.createArrayNode();
            for (JsonNode term : terms) {
                String qs = String.format(COURSE_QS, term.get("term"));
                courses.addAll(query(ldapConnectionPool, qs, COURSE_FILTER, SearchScope.ONELEVEL));
            }
            log.info("Collected {} courses", courses.size());
            writeToJson(Paths.get("uAlbertaHarvesterDump/courses.json"), courses);

            // Classes
            ExecutorService executorService = Executors.newFixedThreadPool(8);

            for (JsonNode term : terms) {
                executorService.submit(() -> {
                    String qs = String.format(CLASS_QS, term.get("term"));
                    try {
                        ArrayNode nodes = query(ldapConnectionPool, qs, CLASS_FILTER, SearchScope.SUBTREE);
                        log.info("Done executing term={} classes_found={}", term.get("term"), nodes.size());
                        writeToJson(Paths.get("uAlbertaHarvesterDump/classes/" + term.get("term").asText() + ".json"), nodes);
                    } catch (CursorException | IOException | LdapException e) {
                        e.printStackTrace();
                    }
                });
            }

            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                e.printStackTrace();
            }
        } catch (LdapException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException | CursorException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static ArrayNode query(LdapConnectionPool connectionPool, String qs, String filter, SearchScope scope) throws LdapException, IOException, CursorException {
        // Build Request
        log.info("Executing qs=" + qs);
        SearchRequest searchRequest = new SearchRequestImpl();
        searchRequest.setBase(new Dn(qs));
        searchRequest.setFilter(filter);
        searchRequest.setScope(scope);
        searchRequest.setSizeLimit(SIZE_LIMIT);

        // Get Connection
        LdapConnection connection = connectionPool.getConnection();

        // Execute
        SearchCursor searchCursor = connection.search(searchRequest);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        while (searchCursor.next()) {
            Response response = searchCursor.get();
            if (response instanceof SearchResultEntry) {
                Entry resultEntry = ((SearchResultEntry) response).getEntry();
                JsonNode node = objectMapper.createObjectNode();
                for (Attribute attribute : resultEntry.getAttributes()) {
                    ((ObjectNode) node).put(attribute.getId(), attribute.getString());
                }
                arrayNode.add(node);
            }
        }
        // Cleanup
        searchCursor.close(); // gotta plug those leaks
        connectionPool.releaseConnection(connection);
        return arrayNode;
    }

    private static void writeToJson(Path path, ArrayNode arrayNode) throws IOException {
        log.info("Writing " + path.toString());
        path.toFile().getParentFile().mkdirs();
        OutputStream outputStream = new FileOutputStream(path.toFile());
        objectMapper.writeValue(outputStream, arrayNode);
    }
}