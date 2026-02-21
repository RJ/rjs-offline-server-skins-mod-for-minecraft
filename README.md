# RJ's Offline Server Skins Mod

Server and client mod for fabric, 1.21.11.

Server config file specifies schema for player skins:

```
# config/rjs_offline_server_skins.txt
skin_url_template=http://your.ip.or.server/skins/%name%.png
```

You are responsible for hosting that webserver and skin files. In my case, that is on the same
machine that hosts the minecraft server, and I use a LAN ip address.

Server injects the correct skin URL into the player profile based on their name.

Players connect, and the clientside part of the mod makes sure to bypass the normal
skin signing checks, to allow unsigned skins to be loaded from any URL.

This allows you to manage skins in a purely offline way without internet access at all.