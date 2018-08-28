$("<link rel='stylesheet' href='../../js/jquery-ui.css'><script src='../../js/jquery-ui.js'></script>").prependTo('div:eq(0)');
$("<div id='dialog' style='display:none'><iframe scrolling='No' frameborder=0 id='myiframe'></ifrmae></div>").prependTo('div:eq(0)');
(function( $, undefined ) {

    // Extends the dialog widget with a new option.
    $.widget( "app.dialog", $.ui.dialog, {
    
        options: {
            iconButtons: []
        },
    
        _create: function() {
    
            // Call the default widget constructor.
            this._super();
    
            // The dialog titlebar is the button container.
            var $titlebar = this.uiDialog.find( ".ui-dialog-titlebar" );
    
            // Iterate over the "iconButtons" array, which defaults to
            // and empty array, in which case, nothing happens.
            $.each( this.options.iconButtons, function( i, v ) {
    
                // Finds the last button added. This is actually the
                // left-most button.
                var $button = $( "<button/>" ).text( this.text ),
                    right = $titlebar.find( "[role='button']:last" )
                                     .css( "right" );
    
                // Creates the button widget, adding it to the titlebar.
                $button.button( { icons: { primary: this.icon }, text: false } )
                       .addClass( "ui-dialog-titlebar-close" )
                       .css( "right", ( parseInt( right ) + 22) + "px" )
                       .click( this.click )
                       .appendTo( $titlebar );
    
            });
    
        }
    
    });
  	

})( jQuery );

function dset()
{
	$("#dialog").dialog({
 		hide : 'clip' ,
		resizable: false,
		dialogClass: 'myclass',
 		width:'auto',
		height:'auto',
		autoOpen: false,
		close: function (event) {
			tmp = "";
			num = 0;
		}, 
		iconButtons: 
		[{
			text: "回上一頁",
                	icon: "	ui-icon-arrowreturnthick-1-w",
                	click: function( e ) {
				tt = $("#myiframe").val().split(',');
				$.get("//"+location.hostname+":8080/next_alpha/Qbase/doo/"+tt[tt.length-2].substr(0, 2)+"/"+tt[tt.length-2] +'.txt', function (data) {
					$("#myiframe").attr('src',"//"+location.hostname+":8080/next_alpha/Qbase/doo/"+tt[tt.length-2].substr(0, 2)+"/"+tt[tt.length-2] + ".html");
					tt.splice(tt.length-1, 1);
					$("#myiframe").val(tt.join(","));
					$("#dialog").dialog({width:'auto',height:'auto',title: data.split('&')[0].slice(8)});
				});
			}
                }]
  
	});
}

var explorer =navigator.userAgent ;
if(explorer.indexOf("Chrome") == -1)
{
	dset();
}
else 
{	
	
	$(function() {

     		dset();
    	});

}

function doo(filename) {
	tmpstr = filename.substr(0, 2);
	if (tmp != filename) {
		$.get('../../doo/' + tmpstr + '/' + filename + '.txt', function (data) {
			tmp = filename;
			data = data.split('&')[0].slice(8);
			$('#myiframe').attr('src', '../../doo/' + tmpstr + '/' + filename + '.html');	
			$('#myiframe').val(filename);
			if(num == 0)
			{
				num = 1;
				$('#dialog' ).dialog( 'option', 'closeText','關閉');
				$('#dialog').dialog({
					title: data,
					autoOpen: true,
				});
				
			}
			else
			{

				$('#dialog').dialog({
					autoOpen: false,
					width:'auto',
					height:'auto',
					title: data
				});
			}
			
		
 		});
	}
}


