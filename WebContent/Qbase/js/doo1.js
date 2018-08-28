
if(num == 0)
{
	$("<div  class='box' style='position:absolute; z-index:1; float:left;display:none;'><h1 style='height:30px; line-height:30px; color:#fff; font-size:14px; background:#87b0e1; padding-left:10px;'><span  id='showstr'></span><span id='closebox' style='float:right; cursor:pointer;'>close&nbsp;&nbsp;&nbsp;</span><span  style=' cursor:pointer; float:right; padding-right:10px;' id='prev'>prev</span></h1><iframe id='myiframe'></iframe><div id='tmp_str'></div></div>").prependTo('body');
	var t = 1;
	num = 1;
}
function doo(filename) {
	if (tmp != filename) {
		var tmpstr = filename.substr(0, 2);
		var url = "//"+location.hostname+"/QBase/doo/"+tmpstr+"/"+filename;
		$.get(url + ".txt", function (data) {
			tmp = filename;
			$("#showstr")[0].innerHTML=data.split('&')[0].slice(8);
			$("#myiframe").attr('src', url + ".html");
			$("#prev").hide();
			$("#tmp_str").val(filename);
			$('.box').show();
			$('#prev').click(function () {
				tt = $("#tmp_str").val().split(',');
				$.get("//"+location.hostname+"/QBase/doo/"+tt[tt.length-2].substr(0, 2)+"/"+tt[tt.length-2] +'.txt', function (data) {
						if(t != null)	{
							$("#showstr")[0].innerHTML=data.split('&')[0].slice(8);
							$("#myiframe").attr('src',"//"+location.hostname+"/QBase/doo/"+tt[tt.length-2].substr(0, 2)+"/"+tt[tt.length-2] + ".html");
							if((tt.length-2) <=0)
								$('#prev').hide();
							tt.splice(tt.length-1, 1);	
							$("#tmp_str").val(tt.join(","));
						}
						
				});
										
			});		
			$('#closebox').click(function () {
				$('.box').hide();
				tmp ='';
				
			});
			$('.box').easydrag();
 		});
	}
}

