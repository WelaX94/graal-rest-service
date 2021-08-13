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
    1. filters
       
       List of values:
        * q - in queue
        * r - running
        * c - execution canceled
        * s - execution successful
        * f - execution failed
          
          You can combine filters or use them individually. The order in which they are placed in the query is the order in which the scripts will be rendered. Scripts equal in state are sorted by time of addition.
          
       **Default sorting**: stopped = successful = failed < running < queue
          
       Query examples:
          > http://localhost:3030/scripts/?filters=rcq
       >
          > http://localhost:3030/scripts/?filters=fr

    2. pageSize
       
       This parameter accepts values from 1 and higher. Specifies the number of scripts shown on one page.
       
       **Default value**: 10
       
       Query example:
       > http://localhost:3030/scripts/?pageSize=23

    3. page
       
       This parameter accepts values from 1 and higher. It's specifies the page number in cases where the total number of scripts is greater than the number of scripts displayed on the page
       
       **Default value**: 1
       
       Query example:
       > http://localhost:3030/scripts/?page=4

  You can combine parameters, use them individually or don't specify them.
    
  Query examples:
    > http://localhost:3030/scripts/?page=2&filters=qrc&pageSize=25
  > 
    > http://localhost:3030/scripts/filters=s&page=4


### Add new script
> PUT http://localhost:3030/scripts/script_name
* Body: script content
* Additional Information: Only Latin letters, hyphens and underscores are allowed in the name. Duplicate names are not allowed. Case is important.
* Parameter (optional)
    * api
        1. f - free
        2. b - blocked

      This parameter specifies the type of API: **blocking** or **non-blocking**
        * Blocking - receives a request to run a script and returns information about the script only after it completes
        * Non-blocking - Receives a request to run a script and returns information about it at the time of creation. Returns HTTP Status ACCEPTED.

      **Default value**: f

      Query example:
      > http://localhost:3030/scripts/newScript?api=b


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
    6. Link to logs page

### Stop the script
> POST http://localhost:3030/scripts/script_name
* Body: null
* Additional Information: You can only stop the script with the status RUNNING

### Delete the script
> DELETE http://localhost:3030/scripts/script_name
* Body: null
* Additional Information: You can delete a script with any status except RUNNING. In this case it has to be stopped first