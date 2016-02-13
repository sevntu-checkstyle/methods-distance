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
    table.find('.horizontal-heading td').css('maxWidth', '30px');
    table.find('tr.horizontal-heading>td').css('height', maxWidth);

    table.find('td.cell').click(function(e) {
        table.find('td.highlightable').removeClass('highlighted');
        var td = e.target;
        var column = $(td).index();
        var row = $(td).parent().index();
        console.log('Column: ' + column, 'Row: ' + row);
        var selector = `td.highlightable:nth-child(${column + 1}),tr:nth-child(${row + 1})>td.highlightable`;
        console.log('Selector: "' + selector + '"');
        table.find(selector).addClass('highlighted');
    });
});
