# uAlbertaHarvester
A small Java project used to scrape the University of Alberta LDAP Server for info.

## Add support for
* ~~Terms~~
* ~~Courses~~
* Classes
* Instructors

## Instructions
```bash
mvn clean package shade:shade
java -jar target/ualberta-harvester-1.0-SNAPSHOT.jar
```

## Artifacts Produced
### `terms.json`
```json
[
    {
      "term": "1420",
      "termTitle": "Winter Term 2013",
      "startDate": "2013-01-07",
      "endDate": "2013-04-12"
    }
]
```

### `courses.json`
```json
[
    {
      "term_id": 1400,
      "course_id": "000037",
      "subject": "ACCTG",
      "subjectTitle": "Accounting",
      "course": "Seminar in Management Accounting",
      "catalog": "624",
      "description": "Seminar consisting of topics concerned at an advanced level with generating and using accounting and related data in the planning and control functions of organizations. Prerequisite: ACCTG 523.",
      "faculty": "Faculty of Business",
      "department": "Department of Accounting, Operations and Information Systems"
    }
]
```