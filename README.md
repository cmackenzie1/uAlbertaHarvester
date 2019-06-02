# uAlbertaHarvester
A small Java project used to scrape the University of Alberta LDAP Server for info.

**Auto Scrape Status**: ![Image](https://travis-ci.org/cmackenzie1/uAlbertaHarvester.svg?branch=auto)

**Keywords**: ualberta, university of alberta, course listings, catalog, calendar

## Downloads
|Name|Description|Size (Approx.)|Link|
|----|----|----|----|
|Terms|A collection of all the terms listed at the UofA|<1 MB|[terms.json](https://github.com/cmackenzie1/uAlbertaHarvester/releases/latest/download/terms.json)|
|Courses|A collection of all the courses listed at the UofA|~50 MB|[courses.json](https://github.com/cmackenzie1/uAlbertaHarvester/releases/latest/download/courses.json)|
|Classes|A collection of all the classes listed at the UofA|~120 MB|[classes.json](https://github.com/cmackenzie1/uAlbertaHarvester/releases/latest/download/classes.json)|

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
      "enddate": "2020-04-08",
      "term": "1700",
      "startdate": "2020-01-06",
      "objectclass": "uOfATerm",
      "termtitle": "Winter Term 2020"
    }
]
```

### `courses.json`
```json
[
    {
      "subjecttitle": "Interdisciplinary Undergraduate & Graduate Courses",
      "career": "UGRD",
      "subject": "INT D",
      "catalog": "225",
      "units": "3.00",
      "coursetitle": "INTERDISCIPLINARY STUDIES",
      "objectclass": "uOfACourse",
      "facultycode": "AR",
      "faculty": "Faculty of Arts",
      "asstring": "INT D 225",
      "course": "107763",
      "departmentcode": "INT D",
      "term": "1700",
      "department": "Office of Interdisciplinary Studies"
    }
]
```

### `classes.json`
```json
[
  {
    "campus": "OFF",
    "session": "Regular Academic Session",
    "classtype": "E",
    "section": "850",
    "classnotes": "Class taught Jan 13 - March 18 in Italy. Restricted to students in the Cortona program.  Contact the Faculty of Arts at <a href=\"https://www.ualberta.ca/arts/programs/study-abroad/school-in-cortona\" target=_blank>uab.ca/cortona</a> for registration assistance. Closed to web registration. Permission is required to drop this class. Please contact cortona@ualberta.ca for assistance. This section is offered in a Cost Recovery format at an increased rate of fee assessment.",
    "units": "3.00",
    "consent": "Department Consent",
    "startdate": "2020-01-13",
    "objectclass": "uOfAClass",
    "capacity": "34",
    "classtopic": "Critical Thinking, Creativity and Complexity",
    "asstring": "INT D 225 LEC 850",
    "enrollstatus": "O",
    "component": "LEC",
    "enddate": "2020-03-18",
    "gradingbasis": "Graded",
    "instructionmode": "In Person",
    "classstatus": "A",
    "course": "107763",
    "term": "1700",
    "location": "CORTONA",
    "class": "97051"
  }
]
```

## Resources and Acknowledgements
* [Apache Directory](http://directory.apache.org/)
* [uAlberta Open Data API](https://sites.google.com/a/ualberta.ca/open-data/home)
* Inspired by: [https://github.com/ChrisChrisLoLo/courseLdapScraper](https://github.com/ChrisChrisLoLo/courseLdapScraper)