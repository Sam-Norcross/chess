# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```



Server design sequence diagram: https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco4LpOzz6K8SwgX0ALnMCBQJig5QIAePKwvuh6ouisTYghGphm6ZIUgatLlqOJpEuGFoctyvIGoKwowK+YiutKSaXmqiGMfa2iOs6BL4SySEoWgJECtoOFyoYLoCdquqZLG-rTiGLHmpGHLRjACnxpJnb-mm6E8tmuaYHpnY-lcAykaMUE1kGs7NuOrbQe2v5djkYDlBU-ZOM0PRTExuxfNO9nzp8MBLpwq7eH4gReCg6B7gevjMMe6SZJg2TmGy5mVNIACiu55fUeXNC0D6qE+3TBY26AuWyenlNVc4mbBlDwZJQn2MlaFJb6mEYtivGJvxFEETA5JgApAZ2TVaDkUybpUaUADiFIwNO0BIAAXvEiQwMkGSpOtM3NSpEayhxjVBptO0EIkQ1SSNC2CUYKDcPJQbTX6N27XNphnUtMhvRShgbVA22-aYpi4WxZxAiW6HJUZCB5q1IIXSUVzflermZRgnneb5EUrlDUXroEkK2ru0IwMto6sqlp4ZeezAY9ey1FaV9ijlVJ21T+9Vo1ddazS18OuQhSHQnToyqLCyCxDLaj9dhD14aNL0TVNTXoPNZoRoUlqrcwYMQ3dSQHX6x0i6dMnnexTrytbqQ-ebavSRrpIwArYBK3LeuUWp5TG7T9Ohp79tFJdoey+7yZC970v08jqPi2ZOOlH5fTc7L4yVP0OcoAAktIecAIy9gAzAALLM3R9CemQGhWnxPDoCCgA2zfAaU9egYXAByo7-LMLhjzAjQHHVsN4x5lSE6WUyF6oecVAXo4l+XVe1zA9eN-qVn3NWUzt533fWb38796OQ+jCPMBjy4E-E6Tr9rjFATYD4UDYNw8ByYYJWKQ0pnnctlDOlRagNC5jzYIfNny9EHqObG1AOywwas7EKswehINGDBNOGNwQwE9HqJWsISGZCVirLE7snr60IpNT6Os-oAyDrTNaptbpPn2odTBotWGEKdpwyGMM6HhnKBQlAZDcEoADotNhcAAHjVHDwq2Mjw7PVUoIziSsS5xz-AnSRVC1DGVMrDHKWdC6b3KBXGu4Vp6nFnuUAAkF5Acvkl4b1LjY7e9jlyvzftFDclg3rIWSDAAAUhAHkMdDABFPiABszMwFszTNUSkd4WiF15jbdAQ5f7ABCVAOAEBkJQFmFY6QKDjjx3FsLGcs1sEFKKSUspFSvH4NTOA6OAAraJaAyF9J5MYtEA1aEyDtgw7W8C5GsUNhydhJtrrgy4XtS2R1mEaP1unaOwi3aiImRHBh0ivGzNUvM4Oa0gHrOUaMPRWzA7aODqOcZZ1Go2iqB3IpsJmmUFadAdpdzpBnINuycoGlfmwEgLcwwlTugwD2n6QwmQxQwEhf8qA0MdK1NTKUIZAzRwpzFl08xECejVLQY4lmBN3GlhfgEwJ5MAheEKV2L0sBgDYF-oQX6wCmaz26ZjXKBUiolVaMYaeBi6l0uJQWJ5xDuB4HIYqrMKBRmqwOW8hV7LYQgsBpEYYEAaDrWVFxSgGhWEXPTIa41wBTXLwefIq1BqjWg1NcwjQMMcXAgkSqolsrKUOwAhS9GhQnHz1peSswkUgA
