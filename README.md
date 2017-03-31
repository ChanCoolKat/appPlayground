Please write the following coding exercise and push the code to github and share the github link once you upload it.
Implement a RESTful API spring-boot application that provides the following APIs:

• API to upload a file with a few meta-data fields. Persist meta-data in persistence store 
(In memory DB or file system and store the content on a file system)

• API to get file meta-data

• API to download content stream (Optional)

• API to search for file IDs with a search criterion (Optional)

• Write a scheduler in the same app to poll for new items in the last hour and send an email (Optional)
=======================================================================================================================================

git clone https://github.com/spring-guides/gs-uploading-files.git
	
- To compile and run Spring app
	mvn spring-boot:run

- To JAR package the Spring app
	mvn clean package
	
- To execute the JAR file
	java -jar target/finra-fileuploader-0.1.0.jar
	
Open browser and enter http://localhost:8080