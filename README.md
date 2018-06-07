Generated with swirlds-hashgraph-archetype.

1. Hashgraph SDK
Make sure Hashgraph SDK is installed in /home/alex/Repositories/swirlds/sdk as described in https://dev.hashgraph.com/docs/installation/

2. Configure the SDK
Change the config.txt file found in the SDK as follows
   * comment the GameDemo.jar line and add a new app line for this application (HashgraphSocket)
```
...
# app,		GameDemo.jar,		   9000, 9000
app,        HashgraphSocket.jar
...
```
   * For now, switch off the TLS encryption for a faster startup. You can come back and revert this change later. Just uncomment the line:
```
TLS, off
```

3. Build it
```
mvn clean install
```
This will package the app jar and copy it to the 'data/apps' dir inside the Hashgraph sdk.

4. Run from IntelliJ IDEA
   * Run -> Edit Configurations...
   * Add new Application cofiguration
   * Main Class: HashgraphSocketMain
   * Working Directory: /home/alex/Repositories/swirlds/sdk
   * Press "Run..." or "Debug..."

You should see four console windows and one main browser window.

Or to run from command line in the usual way, go to hashgraph sdk dir
```
java -jar swirlds.jar
```

NOTE: Every code change needs a 'mvn clean install' before you run the app again
