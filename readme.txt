Our second semester project, a p2p chat system made using java sockets for backend and javafx for UI. 
The system has 2 roles Server ans client. when a server is started pto 8 clients can simltaneosly join and chat there.
the server has a few custom commands. the server owner can ban any client sing the server /ban command disconnecting the client and ending their session.
the clients can send private message to each other not visible to other clients through /message commmand. We have also integrated emojis and multiple themes for the UI.

the requirements include:
* javasdk
* 4 GB ram
* i3 processor
* 150 mb Storage
* javajdk
* a code editor

Instruction:
run the server.bat file to initialize and start the central server
run client.bat file to add the first client, a popup will appear asking for a username , enter username and the client will be registered.
run client.bat multiple times to add multiple Clients.?
to ban a client goto server terminal , type /@ban username and hit enter the client session will be terminated.
to send private message to a specific client type /@message username then type the message , This message will bhe logged into the server console but wont how for other clients.
