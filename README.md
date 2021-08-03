# GraalJS REST Service
This service allows you to run arbitrary JavaScript code and get the result of its execution.

## Methods for working with the service


### Service location
* GET http://localhost:3030

### List of all scripts
* GET http://localhost:3030/scripts

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