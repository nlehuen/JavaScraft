if (typeof Player === 'undefined') {
    var Player = {};
}

Player.render = function() {
    var pilotHead = document.createElement('div');
    pilotHead.id = 'pilotHead';
    pilotHead.style.position = 'fixed';
    pilotHead.style.bottom = '0';
    pilotHead.style.left = '50%';
    pilotHead.style.transform = 'translateX(-50%)';
    pilotHead.style.width = '100px';
    pilotHead.style.height = '50px';
    pilotHead.style.backgroundColor = '#333';
    pilotHead.style.borderTopLeftRadius = '50px';
    pilotHead.style.borderTopRightRadius = '50px';
    pilotHead.style.zIndex = '1000';
    pilotHead.style.pointerEvents = 'none';

    // Check if it already exists to avoid duplicates
    if (!document.getElementById('pilotHead')) {
        document.body.appendChild(pilotHead);
    }
};
