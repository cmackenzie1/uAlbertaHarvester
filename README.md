# uAlbertaHarvester
A small Java project used to scrape the University of Alberta LDAP Server for info.

Auto Scrape Status: ![Image](https://travis-ci.org/cmackenzie1/uAlbertaHarvester.svg?branch=auto)

## Downloads
|Name|Description|Size (Approx.)|Link|
|----|----|----|----|
|Terms|A collection of all the terms listed at the UofA|<1 MB|[terms.json](https://github.com/cmackenzie1/uAlbertaHarvester/releases/latest/download/terms.json)|
|Courses|A collection of all the courses listed at the UofA|~11 MB|[courses.json](https://github.com/cmackenzie1/uAlbertaHarvester/releases/latest/download/courses.json)|
|Classes|A collection of all the classes listed at the UofA|~100 MB|[classes.json](https://github.com/cmackenzie1/uAlbertaHarvester/releases/latest/download/classes.json)|

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

## Resources and Acknowledgements
* [uAlberta Open Data API](https://sites.google.com/a/ualberta.ca/open-data/home)
* Inspired by: [https://github.com/ChrisChrisLoLo/courseLdapScraper](https://github.com/ChrisChrisLoLo/courseLdapScraper)