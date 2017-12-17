var Block = Java.type('cn.nukkit.block.Block');
var Vector3 = Java.type('cn.nukkit.math.Vector3');

function v(x, y, z) {
  return new Vector3(x, y, z);
}

function b(name) {
   return Block.get(Block[name]);
}

function broadcast(message) { server.broadcastMessage(message); }

function move(player, x, y, z) { player.setPosition(new Vector3(x, y, z)); }

function slab(radius, matter, player) {
  var w = b(matter || 'STONE');
  var p = (player || me).getPosition();
  radius = radius || 5;
  for (var i=1-radius; i<radius; ++i) {
    for (var j=1-radius; j<radius; ++j) {
      level.setBlock(v(p.x+i, p.y-1, p.z+j), w);
    }
  }
}
