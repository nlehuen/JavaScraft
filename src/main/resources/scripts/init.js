var Block = Java.type('cn.nukkit.block.Block');
var Vector3 = Java.type('cn.nukkit.math.Vector3');

function v(x, y, z) {
  return new Vector3(x, y, z);
}

function b(name) {
   return Block.get(Block[name]);
}

function broadcast(message) {
  server.broadcastMessage(message);
}

function move(player, x, y, z) {
  player.setPosition(new Vector3(x, y, z));
}

function applyDefaults(params, defaults) {
  params = params || {};
  params.player = params.player || me;
  params.level = params.level || level;
  params.pos = params.pos || params.player.position;
  params.matter = Block.get(Block[params.matter || 'GLASS']);
  params.inside = Block.get(Block[params.inside || 'AIR']);
  if (defaults) {
    for(var k in defaults) {
      if (!params.hasOwnProperty(k)) {
        params[k] = defaults[k];
      }
    }
  }
  return params;
}

function slab(params) {
  params = applyDefaults(params, {r:5});
  for (var i=-params.r; i<=params.r; ++i) {
    for (var j=-params.r; j<=params.r; ++j) {
      params.level.setBlock(v(params.pos.x+i, params.pos.y-1, params.pos.z+j), params.matter);
    }
  }
}

function box(params) {
  params = applyDefaults(params, {w:5, d:5, h:10});
  for (var i=-params.w; i<=params.w; ++i) {
    for (var j=-params.d; j<=params.d; ++j) {
      for (var k=-1; k<params.h; ++k) {
        var b = (k == -1 || k == params.h - 1 || i == -params.w || i == params.w || j == -params.d || j == params.d) ?
                params.matter : params.inside;
        params.level.setBlock(v(params.pos.x+i, params.pos.y+k, params.pos.z+j), b);
      }
    }
  }
}

function sphere(params) {
  params = applyDefaults(params, {r:5});
  for (var i=-params.r; i<=params.r; ++i) {
    for (var j=-params.r; j<=params.r; ++j) {
      for (var k=-params.r; k<params.r; ++k) {
        var d = Math.sqrt(i*i + j*j + k*k);
        if (d <= params.r) {
            var b = (params.r - d) < 1 ? params.matter : params.inside;
            params.level.setBlock(v(params.pos.x+i, params.pos.y+params.r+k-1, params.pos.z+j), b);
        }
      }
    }
  }
}

function tower(params) {
  params = applyDefaults(params, {r:5, h:20});
  for (var i=-params.r; i<=params.r; ++i) {
    for (var j=-params.r; j<=params.r; ++j) {
      var d = Math.sqrt(i*i + j*j);
      if (d <= params.r) {
        for (var k=-1; k<=params.h; ++k) {
          var b = (k == -1 || k == params.h || params.r-d < 1) ? params.matter : params.inside;
          params.level.setBlock(v(params.pos.x+i, params.pos.y+k, params.pos.z+j), b);
        }
      }
    }
  }
}

function onPlayerLoginEvent(ple) {
  server.broadcastMessage('Welcome to JavaScraft, ' + ple.player.name);
}

function onBlockPlaceEvent(bpe) {
  // Spawn a sphere of leaves whenever a player stack two Oak Wood blocks
  if (bpe.block.id == Block.WOOD
      && bpe.blockAgainst.id == Block.WOOD
      && bpe.block.y == bpe.blockAgainst.y + 1) {
      sphere({pos:v(bpe.block.x, bpe.block.y + 2, bpe.block.z), matter:'LEAVES'});
  }
}
