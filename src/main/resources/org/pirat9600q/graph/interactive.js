jQuery(function($) {
    var table = $('#matrixTable');
    var maxWidth = table.find('.horizontal-heading td')
        .get()
        .map(function(elem) {
            return $(elem).width();
        })
        .sort(function(l, r) {
            return parseInt(l) - parseInt(r);
        })
        .pop();
    console.log('Max width:' + maxWidth);
    table.css('marginTop', maxWidth + 50 + 'px');
    table.find('.horizontal-heading td').css('maxWidth', '30px');
});
