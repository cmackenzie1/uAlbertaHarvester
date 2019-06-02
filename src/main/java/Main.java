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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import model.Course;
import model.Term;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class Main {
    private static final String LDAP_HOST = "directory.srv.ualberta.ca";
    private static final String ROOT_DN = "dc=ualberta, dc=ca";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Collection<Term> terms = new ArrayList<>();
        Collection<Course> courses = new ArrayList<>();
        LdapConnection connection = new LdapNetworkConnection(LDAP_HOST, 389);
        try {
            log.info("Attempting to connect");
            connection.bind();
            assert connection.isConnected();
            log.info("Connection successful");

            // Terms
            log.info("Querying Terms...");
            queryTerms(terms, connection);
            log.info("Done Querying Terms...");

            // Courses
            log.info("Querying Courses...");
            for (Term term : terms) {
                queryCourses(courses, term.getTerm(), connection);
            }
            log.info("Done Querying Courses...");

            connection.close();

            writeToJson(Paths.get("terms.json"), terms);
            writeToJson(Paths.get("courses.json"), courses);

        } catch (LdapException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static void queryCourses(Collection<Course> courses, String term, LdapConnection connection) throws LdapException {
        String qs = String.format("term=%s,ou=calendar,dc=ualberta,dc=ca", term);
        EntryCursor cursor = connection.search(qs, "(objectclass=uOfACourse)", SearchScope.ONELEVEL);
        for (Entry entry : cursor) {
            log.info(entry.toString());
            Course course = Course.builder()
                    .term_id(Integer.parseInt(entry.get("term").getString()))
                    .course_id(entry.get("course").getString())
                    .subject(entry.get("subject").getString())
                    .subjectTitle(entry.get("subjectTitle").getString())
                    .course(entry.get("courseTitle").getString())
                    .catalog(entry.get("catalog").getString())
                    .faculty(entry.get("faculty").getString())
                    .department(entry.get("department").getString())
                    .build();
            try {
                course.setDescription(entry.get("courseDescription").getString());
            } catch (NullPointerException e) {
                course.setDescription("");
            }
            courses.add(course);
        }
    }

    private static void queryTerms(Collection<Term> terms, LdapConnection connection) throws LdapException {
        String qs = String.format("ou=calendar,dc=ualberta,dc=ca");
        EntryCursor cursor = connection.search(qs, "(objectclass=uOfATerm)", SearchScope.ONELEVEL);
        for (Entry entry : cursor) {
            terms.add(new Term(
                    entry.get("term").getString(),
                    entry.get("termTitle").getString(),
                    entry.get("startDate").getString(),
                    entry.get("endDate").getString()
            ));
        }
    }

    private static void writeToJson(Path path, Collection collection) throws IOException {
        log.info("Dumping " + path.toString());
        OutputStream outputStream = new FileOutputStream(path.toFile());
        objectMapper.writeValue(outputStream, collection);
    }
}