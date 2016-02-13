jQuery(function($) {
    function responsiveTable(tableSelector) {
        var table = $(tableSelector);
        var allHighlightableCells = table.find('td.highlightable');
        var firstColumnCells = table.find('td:first-child');
        table.find('td.cell').click(function(e) {
            allHighlightableCells.removeClass('highlighted');
            firstColumnCells.removeClass('dependency');
            var td = $(e.target);
            var clickedColumn = td.index();
            var clickedRow = td.parent().index();
            var cellsInRowOrColumn = `td.highlightable:nth-child(${clickedColumn + 1}),tr:nth-child(${clickedRow + 1})>td.highlightable`;
            table.find(cellsInRowOrColumn).addClass('highlighted');
            table.find(`tr:nth-child(${clickedColumn + 1})>td:first-child`).addClass('dependency');
        });
    }

    function setUpTableDimensions(tableSelector) {
        var table = $(tableSelector);
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
    }

    var tableSelector = '#matrixTable';
    setUpTableDimensions(tableSelector);
    responsiveTable(tableSelector);
});
