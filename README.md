# SocialNetwork

This project was developed as part of a master's thesis in Mathematics and Computer Science at the
Faculty of Science, University of Zagreb. The thesis, titled "JHipster," was completed by Mihael Petrinjak
under the supervision of doc. dr. sc. Matej Mihelčić.

## Summary

Modern web applications are large and rich with various functionalities, which makes the initial phases of implementation time-consuming, especially due to boilerplate (necessary and repetitive) code and environment and tool configurations. Application specifications are often written in human-readable languages and not directly translated into executable code, which complicates the connection between requirements and implementation. Large applications consist of many interconnected components and require final integration testing based on specifications to ensure system correctness beyond isolated units.

JHipster is a command-line tool presented as a platform for rapid generation and development of modern web applications. Quality development implies indispensable stages: specification and design, implementation, and testing. This work examines the benefits a development team can gain from JHipster in each of these stages. For practical analysis, two applications are designed. The first, named "MHipster," is conceived as an adaptation of JHipster into a web platform with certain adjustments. On one hand, the MHipster user’s freedom in choosing tools for developing their application is reduced. On the other hand, for the fixed set of tools offered by JHipster, functional requirements and use cases are composed so that the user experience of MHipster emphasizes even faster application development with JHipster’s help. More precisely, through a graphical interface, the user can manage objects used for specification, projects, and test reports. The main advantage of MHipster is the generation of reports on integration tests with a measured ratio of generated to manually written source code.

To demonstrate the use of MHipster, the "SocialNetwork" application is developed. It has key features common to modern social networks. Such a type of application is one of the most prevalent in the current market and seems like a good choice.

Using tools provided by JHipster for writing specification and design, it is easy to write documents understandable to non-programmers, which JHipster can interpret and directly convert into project code. Adapting the generated code thoroughly to business requirements does not take much effort. Roughly speaking, less than 20\% of the source code of the mentioned applications is manually written across almost all measurement categories. This does not include the initial parts of the project that serve environment and dependency configurations. On the other hand, a significant portion of the generated source code in these measurements was unused.

JHipster automates managing complex configuration settings such as setting up the application framework and project dependencies, thereby accelerating the initial development and deployment process. Additionally, in modern web applications, a large part of the source code, unrelated to the business domain and logic, is mostly the same. JHipster solves the boilerplate code problem in the layered and component-oriented architectural style standard for large and maintainable applications by efficiently generating predictable code parts based on a given specification understandable to non-programmers. Moreover, part of the specification is used to automatically generate and run integration tests down to the details of business logic.

Of course, the design of MHipster assumes that choosing alternative tools that JHipster offers would not significantly affect the analysis because they are similar to their selected competitors. Also, although sufficiently rich, there is room for improvement in both demonstration applications.

## Documentation

Documentation can be found in the thesis.

Related app: [MHipster](https://github.com/mihapetr/MHipster2).
