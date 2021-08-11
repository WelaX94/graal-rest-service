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


## Methods for working with the service

### Service location
* GET http://localhost:3030

### List of all scripts
* GET http://localhost:3030/scripts
* Additional Information: You can see the list of all scripts, their execution status, and assigned address

### Add new script
* PUT http://localhost:3030/scripts/script_name
* Body: script content
* Additional Information: Only Latin letters, hyphens and underscores are allowed in the name. Duplicate names are not allowed. Case is important.

### Get information about the script
* GET http://localhost:3030/scripts/script_name
* Additional Information: You can see the execution status of the script, and its logs

### Stop the script
* POST http://localhost:3030/scripts/script_name
* Body: null
* Additional Information: You can only stop the script with the status RUNNING

### Delete the script
* DELETE http://localhost:3030/scripts/script_name
* Body: null
* Additional Information: You can delete a script with any status except RUNNING. In this case it has to be stopped first