# VDB Manager

## Summary

The VDB Manager web application is for use with Teiid, for the purpose of managing dynamic VDBs.

This application is limited in functionality and was written for the purpose of demoing Teiid Dynamic VDB capabilities on OpenShift.  The application can also be used on a standalone local instance.  The Teiid Designer tooling offers a more complete functionality for VDB deployment.

For more information on Teiid Desginer, including getting started guides, reference guides, and downloadable binaries, visit the project's website at [http://www.jboss.org/teiiddesigner/](http://www.jboss.org/teiiddesigner/)
or follow us on our [blog](http://teiid.blogspot.com/) or on [Twitter](https://twitter.com/teiiddesigner). Or hop into our [IRC chat room](http://www.jboss.org/teiiddesigner/chat)
and talk our community of contributors and users.

## Building the Application

Clone this repo to your system, then build the application war 

$mvn clean install -s settings.xml

This will generate the .war file into the target directory, which you can then drop into your JBoss deployments directory.
Note : The settings.xml file is included, but you will need to modifiy it.  First, install the EAP 6.1 repo locally - then modify settings.xml to reference it - (see Dependencies section)

## Dependencies

The pom.xml provided has dependencies to JBoss EAP 6.1 and Teiid 8.4.1 currently.

 - EAP 6.1 - the maven repo for EAP 6.1 Final can be downloaded from http://www.jboss.org/products/eap.html and installed into your local maven repo
 - Teiid   - the public maven repos for Teiid are located at https://repository.jboss.org/nexus/content/groups/public/org/jboss/teiid/

## Access the application

Once deployed you may access the application in your browser at:

http://[host]/vdbmanager

for example: 

http://localhost:8080/vdbmanager

