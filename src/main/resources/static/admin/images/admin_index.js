function openWin(_TITLE,_ID,_URL) {
	$("#framesDiv iframe").hide();
	$("#tab li input").removeClass("current");
	if($("#"+_ID).length==0) {
		//var menLi = '<li id="'+_ID+'Menu"><i class="close" title="关闭"></i><input class="current inputMenu" value="'+_TITLE+'" hidefocus="" type="button"></li>';
		var menLi = '<li id="'+_ID+'Menu" style="width: 110px" ><input id="'+_ID+'_input" class="current inputMenu" value="'+_TITLE+'" hidefocus="" type="button"></li>';
		$("#tab").append(menLi);
		$("#framesDiv").append('<iframe style="width:100%;height:100%" id="'+_ID+'" name="'+_ID+'" src="'+_URL+'" frameborder="no" noresize="noresize" />');
	} else {
		$("#framesDiv iframe#"+_ID).show();
		$("#tab li#"+_ID+"Menu").show();
		$("#tab li#"+_ID+"Menu input").addClass("current");
		$("#"+_ID).attr('src', $("#"+_ID).attr('src'));
	}   
}
function init() {
	$("#framesDiv").append('<iframe style="width:100%;height:100%;" id="iframeContentIdex"  name="iframeContentIdex"  src="/admin/iframeIndex" frameborder="no"  noresize="noresize" />');
}
$(document).ready(function(){
	$("#tab").click(function(event){
		  if (event.target.className == "close") {
			  var iframeID = $(event.target).parent().attr("id").replace("Menu","");
			  $("#"+iframeID).hide();
			  $($(event.target).parent()).hide();
			  
			  if($("#tab li:visible input.current").length>0) return;  
			  
			  var prevIframeID = $($("#tab li:visible:last")[0]).attr("id").replace("Menu","");
			  $("#"+prevIframeID).show();  
			  $("#tab li#"+prevIframeID+"Menu").show();
			  $("#tab li#"+prevIframeID+"Menu input").addClass("current");
			  
		  } else if (event.target.className == "inputMenu") {
		  	  $("#framesDiv iframe").hide();
			  $("#tab li input").removeClass("current");
			  var iframeID = $(event.target).parent().attr("id").replace("Menu","");
			  $("#"+iframeID).show();
			  $($(event.target).parent()).show();
			  $(event.target).addClass("current");   
		  }
	});
	
	$("#tab").dblclick(function(event){
		  if($(event.target)[0].tagName=="INPUT") {
			  var iframeID =$(event.target).attr("id").replace("_input","");
			 // $("#"+iframeID+"_input").hide();
			  $("#"+iframeID+"_input").parent().hide(); 
			  
			  if($("#tab li:visible input.current").length>0) return;  
			  
			  var prevIframeID = $($("#tab li:visible:last")[0]).attr("id").replace("Menu","");
			  $("#"+prevIframeID).show();  
			  $("#tab li#"+prevIframeID+"Menu").show();
			  $("#tab li#"+prevIframeID+"Menu input").addClass("current");
		  }	
	});
	
	init();
});
