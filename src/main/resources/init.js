var v = Java.type('cn.nukkit.math.Vector3');

function broadcast(message) { server.broadcastMessage(message); }

function move(player, x, y, z) { player.setPosition(new v(x, y, z)); }