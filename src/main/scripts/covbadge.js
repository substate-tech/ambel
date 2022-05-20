var lcov2badge = require('/usr/app/node_modules/lcov2badge/');
lcov2badge.badge('./coverage/lcov.info', function(err, svgBadge){
    if (err) throw err;
    console.log(svgBadge);
});
