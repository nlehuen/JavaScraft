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

function slab(params) {
  params = params || {};
  var player = (params.player || me);
  var pos = player.position;
  var level = player.level;
  var matter = Block.get(Block[params.matter || 'STONE']);
  var radius = params.radius || 5;
  for (var i=1-radius; i<radius; ++i) {
    for (var j=1-radius; j<radius; ++j) {
         level.setBlock(v(pos.x+i, pos.y-1, pos.z+j), matter);
    }
  }
}

function tow(params) {
  params = params || {};
  var player = (params.player || me);
  var pos = player.position;
  var level = player.level;
  var matter = Block.get(Block[params.matter || 'STONE']);
  var inside = Block.get(Block[params.inside || 'AIR']);
  var radius = params.radius || 5;
  var height = params.height || 20;
  for (var i=1-radius; i<radius; ++i) {
    for (var j=1-radius; j<radius; ++j) {
      var dist = Math.sqrt(i*i + j*j);
      if (dist <= radius) {
        for (var k=-1; k<=height; ++k) {
         level.setBlock(v(pos.x+i, pos.y+k, pos.z+j),
                        (k == -1 || k == height || radius-dist <= 1) ? matter : inside);
        }
      }
    }
  }
}

function onPlayerLoginEvent(ple) {
  server.broadcastMessage('Welcome to JavaScraft, ' + ple.player.name);
}