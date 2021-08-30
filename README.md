# GraalJS REST Service
This service allows you to run arbitrary JavaScript code and get the result of its execution.

## Tech assignment
Write a REST API shell around GraalJs, which would allow through the api

1) run arbitrary javascript code in graaljs, passed in the request body, and return in the response body the script output to the console or an error message;
2) view the script's status (completed successfully, with error, running, queued) and its console output at the time of the request;
3) stop hanging scripts forcibly;
4) view the list of completed and running scripts;
5) delete completed scripts from the list

Requests can come in parallel. A script can run for a long time or freeze in an endless loop - we have no control over the scripts content.


## Requirements for launching
* [Java JDK 11](https://openjdk.java.net/)

   or

* [GraalVM 21.2.0 (Java 11 based)](https://www.graalvm.org/)


## Service Launch
1. Build service in Jar file
   > mvn cln package
2. Run service
   > java -jar ./target/graal-rest-service-0.0.1-SNAPSHOT.jar


### Service location
> GET http://localhost:3030
* Additional Information: You can use it to check the status of the server. Returns its own link and a link to the list of scripts.

### Swagger documentation
> GET http://localhost:3030/swagger-ui

### List of all scripts
> GET http://localhost:3030/scripts
* Additional Information: You can see the list of all scripts in JSON format, their execution status, and assigned address
* List of parameters (optional)
    1. Filtration
        * status - allows you to filter the list of received scripts by a specified status. Options:
            * in_queue
            * running
            * execution_canceled
            * execution_successful
            * execution_failed
          
            If the parameter is not specified, scripts will be output regardless of their status.
        * nameContains - allows you to filter the list of scripts by a pattern, which must contain the script name

          If the parameter is not specified, scripts with all names will be displayed

       These filters can be used alone or in combination.
          
       Query examples:
          > http://localhost:3030/scripts?status=running
       >
          > http://localhost:3030/scripts?nameContains=aer&status=execution_canceled

    2. Sorting
        * orderByName - allows you to filter the list of received scripts by a specified status. Options:
            * false
            * true

          Boolean parameter. If it is not explicitly specified, it takes false value and sorting is performed by creation date.
          If true, scripts are sorted by script name
        * reverseOrder - allows you to wrap the selected sorting type. Options:
            * false
            * true

          If it is not explicitly specified, it takes false value

       These sorting parameters can be used alone or in combination.

       Query examples:
       > http://localhost:3030/scripts?reverseOrder=false
       >
       > http://localhost:3030/scripts?orderByName=true&reverseOrder=true

    3. Pagination

        * pageNumber - page number from the requested script list. If no value is set, it becomes 1
        * pageSize - parameter specifies the maximum number of scripts shown on the page. If no value is set, it becomes 10

       Query examples:
       > http://localhost:3030/scripts?pageSize=50
       >
       > http://localhost:3030/scripts?pageNumber=4&pageSize=15

       
  You can combine parameters, use them individually or don't specify them.
  Query examples:
>   http://localhost:3030/scripts?reverseOrder=true&status=execution_successful&pageNumber=5
> 
>   http://localhost:3030/scripts?pageSize=13&orderByName=true


### Add new script
> PUT http://localhost:3030/scripts/script_name
* Body: script content
* Additional Information: Only Latin letters, hyphens and underscores are allowed in the name. Duplicate names are not allowed. Case is important.


### Add a script to display logs in real time
> PUT http://localhost:3030/scripts/script_name/logs
* Body: script content
* Additional Information: The second version of the script running request. In this case the logs will be displayed in real time.


### Get information about the script
> GET http://localhost:3030/scripts/script_name
* Returns JSON with information :
    1. Script name
    2. Execution status
    3. Creation time
    4. Start time (if available)
    5. End time (if available)
    6. Logs size
    7. Link to logs page
    8. Link to script body page
    9. Self link
    10. Script list link


### Get script logs
> GET http://localhost:3030/scripts/script_name/logs
* Body: null
* Additional Information: returns the output logs of a script
* Parameters:
   * from - the beginning index, inclusive.
   * to - the ending index, exclusive.
  
     By default, it returns full logs,
     but you can limit the boundaries from-to if necessary (either together or separately).
     Important: The numbering of the logs starts from 0.

    Query examples
    > http://localhost:3030/scripts/script_name/logs?from=150&to=12900
  > 
    > http://localhost:3030/scripts/script_name/logs?to=25000

### Get script body
> GET http://localhost:3030/scripts/script_name/script
* Body: null
* Additional Information: returns script body


### Stop the script
> POST http://localhost:3030/scripts/script_name
* Body: null
* Additional Information: You can only stop the script with the status RUNNING


### Delete the script
> DELETE http://localhost:3030/scripts/script_name
* Body: null
* Additional Information: You can delete a script with any status except RUNNING. In this case it has to be stopped first