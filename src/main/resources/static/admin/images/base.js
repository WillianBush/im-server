
String.prototype.trim = function() {
	return this.replace(/(^\s*)|(\s*$)/g, "");
}


var $req = function(_dataType) {
	function method(_method, _url, _data,_callBack) {
		var resultData;
		$.ajax({
			url: _url,
			data: _data,
			type: _method,
			dataType: _dataType,
			cache: false,
			async: false,
			success: function(data) {
				resultData = data;
				if(null!=_callBack&&typeof(_callBack)!="undefined") {
					_callBack(data);
				}
			}
		});
		return resultData;
	}
	return {
		get:function(url,callBack) {
			return method("GET",url,null,callBack);
		},
		post:function(url,data,callBack) {
			return method("POST",url,data,callBack);
		}
		
	}
}
var $reqJSON = new $req("json");
var $reqTXT = new $req("text");
var $reqXML = new $req("xml");
	
	
	
	
	



function loadingShowBg() {
	//var top = $(window).height()/2-96/2;
	//var left = $(window).width()/2-96/2;
	//var loading = "<div id='loadingBgDiv'><div style='position: fixed;background:#B2B2B2; z-index: 999999;filter:alpha(Opacity=80);-moz-opacity:0.5;opacity: 0.5;left:0;top:0;width:100%;height:100%'></div><div style='position: fixed;top:"+top+"px;left:"+left+"px;    z-index: 9999999;'><img src='/images/T1Pk2NXe8hXXXH_oTs-96-96.gif'/></div></div>";
	//$("body").append(loading);
	$body = $("body");
	messageHtml = '<div id="loadingDiv" class="baseMessage" style="text-align: center;font-size: 13px;color: red;top: 30%;left:10%"><div class="messageContent messagewarnIcon">正在执行中,请稍等...</div></div>';
	$body.append(messageHtml);
	var $message = $("#loadingDiv");
	$message.css({"margin-left": "-" + parseInt($message.width() / 2) + "px"}).show();
	
}
   
function loadingHideBg(type,content) {
	$("#loadingDiv").remove();
	//var $message = $("#loadingDiv");
	//$($message).find(".messageContent").text(content);
	//$($message).find(".messageContent").addClass("message"+type+"Icon");
	/**
	setTimeout(function() {
		$message.animate({left: 0, opacity: "hide"}, "slow", function() {
			$message.remove();
		});
	}, 2000);     **/
}
//去除html格式
function removeHTML(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/ /ig,'');//去掉 
    return str;
}

$(function(){
	
	
	

	
	$('.inputFileBtn').click(function(){
		var _this = this;
		var inputName = $(_this).attr("inputName");
		var upload = $(_this).attr("upload");
		var showTo = $(_this).attr("showTo");
		var fileType = $(_this).attr("fileType");
		var fileMaxLen = $(_this).attr("fileMaxLen");
		var tipName = $(_this).attr("tipName");
		var input = $("<input type='file' name='file' tipName='"+tipName+"'/> ");
		if($("#uploadForm").length==0) {
			var uploadForm = '<form id="uploadForm" style="display:none" action="'+upload+'" enctype="multipart/form-data" method="post"></form>';
			$("body").append(uploadForm);
		} 
		$("#uploadForm").empty();
		$("#uploadForm").append($(input));
		input.click();
		$(input).change(function(){
			var flag = checkFile(this,fileType,fileMaxLen);
			if(!flag) return false; 
			loadingShowBg();
			$("#uploadForm").ajaxSubmit({dataType:"json",success:function(data){
				loadingHideBg();
				if( data.status=="success") {
					if($(_this).parent().find("input[name='"+inputName+"']").length==0) {
						$(_this).parent().append('<input type="hidden" name="'+inputName+'" value="'+data.msg+'"/>');
						$("#"+showTo).empty();
						$("#"+showTo).append('<img  src="'+data.msg+'" style="height:100px;" />');
					} else {
						$($(_this).parent().find("input[name='"+inputName+"']")[0]).val(data.msg);
					}
				}
			}}); 
		});
		
	});
	
	
	
	
	$("#searchButton").click(function(){
		loadingShowBg();
	});
	
	var allowAble = true;//允许某操作 防止重复某操作
	$('input[type=submit]').click(function(){
		if(!allowAble) {
			return false;
		}
		var _form = $(this).parents("form").get(0);
		if(null!=_form&&_form!=undefined&&allowAble) {
			allowAble = false;
			checkFormData(_form);
			allowAble = true;
		}
		return false;
	});	
	
	
});  



//打印当前页面内容
function doPrint(_this) {
	var css= "<style type='text/css'>@media print {.NoPrint { display: none; }}</style>";
	_this.className="NoPrint"; 
	window.document.body.innerHTML+=css;
	window.print();
}
function addEventForDom(obj,eventName,fn) {
	 //obj.attachEvent('onfocus', add); // 在原先事件上添加
    //obj.setAttribute('onfocus',add); // 会替代原有事件方法
    //obj.onfocus=add;                 // 等效obj.setAttribute('onfocus',add);        
    if(window.addEventListener) {
        //其它浏览器的事件代码: Mozilla, Netscape, Firefox
        //添加的事件的顺序即执行顺序 //注意用 addEventListener 添加带on的事件，不用加on
    	 if(eventName.indexOf("on")==0) eventName = eventName.substring(2,eventName.length);
    	 obj.addEventListener(eventName, function() {
        	 fn(obj);
         }, false);
    } else {
    	if(eventName.indexOf("on")!=0) eventName = "on"+eventName;
        //IE 的事件代码 在原先事件上添加 add 方法
         obj.attachEvent(eventName,function() {
        	 fn(obj);
         });       
    }
}
/**
 * 事件添加
 * @param domid {Elementid} 控件id 
 * @param eventName {String} 事件名称
 * @param fn {Number} 事件函数
 */
function addEventForDomid(domid,eventName,fn) {
	var obj = get(domid);
	addEventForDom(obj,eventName,fn);
}

/**
 * 图片按比例自适应缩放
 * @param objImgId {Elementid} 用户上传的图片
 * @param maxWidth {Number} 预览区域的最大宽度
 * @param maxHeight {Number} 预览区域的最大高度
 */
function resizeImage(maxWidth,maxHeight,objImgId){
	var objImg = get(objImgId);
	var img = new Image();
	img.src = objImg.src;
	var hRatio;
	var wRatio;
	var Ratio = 1;
	var w = img.width;
	var h = img.height;
	wRatio = maxWidth / w;
	hRatio = maxHeight / h;
	if (maxWidth ==0 && maxHeight==0){
		Ratio = 1;
	}else if (maxWidth==0){//
		if (hRatio<1) Ratio = hRatio;
	}else if (maxHeight==0){
		if (wRatio<1) Ratio = wRatio;
	}else if (wRatio<1 || hRatio<1){
		Ratio = (wRatio<=hRatio?wRatio:hRatio);
	}
	if (Ratio<1){
		w = w * Ratio;
		h = h * Ratio;
	}
	objImg.height = h;
	objImg.width = w;
}
function get(_id) {
	return document.getElementById(_id);
}



//根据特别的属性返回数组
//element:元素名如input
function getEls(element) {
	var arr = document.getElementsByTagName(element);
	return arr;
}

//根据特别的属性返回数组
//element:元素名如input
function getEls(obj,element) {
	var arr = obj.getElementsByTagName(element);
	return arr;
}
//根据特别的属性返回数组
//element:元素名如input
//elementAttribute:控件属性
//elementValue:控件属性值
function getEls(obj,element,elementAttribute,elementValue) {
	var arr = obj.getElementsByTagName(element);
	var newArr = new Array();
	var k=0;
	for(var i=0;i<arr.length;i++) {
		if(arr[i].getAttribute(elementAttribute)==elementValue) {
			newArr[k] = arr[i];
			k++;
		}
	}
	return newArr;
}

//手机验证
function checkTel(obj) {
	var msgDom = $(obj).attr("msgDom");
	if (!/(^0{0,1}1[3|4|5|6|7|8|9][0-9]{9}$)/.test($(obj).val())) {
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+"格式不正确!");
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+"格式不正确!", modal: true, autoCloseTime: 3000});
		 }
		return false;
	}
	 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
	return true;
	
}
//邮箱验证
function checkEmail(obj) { 
	var msgDom = $(obj).attr("msgDom");
	if (!/^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/.test($(obj).val())) {
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+"格式不正确!");
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+"格式不正确!", modal: true, autoCloseTime: 3000});
		 }
		
		return false;
	}
	if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
	return true;
}

//金钱验证
function checkMoney(obj) {
	var msgDom = $(obj).attr("msgDom");
	if(!/(^[1-9]+(.[0-9]{1,2})?$)|(^[0-9]+(.[0-9]{1,2})?$)/.test($(obj).val())) {
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+"格式不正确!");
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+"格式不正确!", modal: true, autoCloseTime: 3000});
		 }
		
		return false;
	}
	if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
	return true;
}

//过滤输入字符的长度   
function check_str_len(obj,min,max){    
	var msgDom = $(obj).attr("msgDom");
    obj.value=obj.value.replace(/(^\s*)|(\s*$)/g, "");   
    var newvalue = obj.value.replace(/[^\x00-\xff]/g, "**");    
    var length11 = newvalue.length;    
    if(length11<min||length11>max){   
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+"的长度范围在"+min+"-"+max+"个字符！");
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+"的长度范围在"+min+"-"+max+"个字符！", modal: true, autoCloseTime: 3000});
		 }
    	
        obj.value="";   
        obj.focus();    
        return false;   
    }    
    if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
    return true;   
}   
  
//验证只能为 正整数   
function checkNumber(obj){   
    var reg = /^[0-9]+$/;   
    var msgDom = $(obj).attr("msgDom");
    if(!reg.test($(obj).val())){
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+'只能输入数字！');
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+'只能输入数字！', modal: true, autoCloseTime: 3000});
		 }
    	
        obj.value = "";   
        obj.focus();   
        return false;   
    }
    if(null!=obj.min&&obj.min.trim()!=""&&obj.min!=undefined) {
    	if(parseInt(obj.value)<parseInt(obj.min)) {
    		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
    			 $("#"+msgDom).html($(obj).attr("tipName")+'不能低于'+obj.min);
    		 } else {
    			 $.dialog({type: "warn", content:$(obj).attr("tipName")+'不能低于'+obj.min, modal: true, autoCloseTime: 3000});
    		 }
    		obj.focus();   
        	return false;  
    	}
    }
    if(null!=obj.max&&obj.max.trim()!=""&&obj.max!=undefined) {
    	if(parseInt(obj.value)>parseInt(obj.max)) {
    		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
    			 $("#"+msgDom).html($(obj).attr("tipName")+'不能大于'+obj.max);
    		 } else {
    			 $.dialog({type: "warn", content:$(obj).attr("tipName")+'不能大于'+obj.max, modal: true, autoCloseTime: 3000});
    		 }
    		
    		obj.focus();   
       		return false;  
    	}
    }
    if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
    return true;
}   
  
//验证数字大小的范围   
  
function check_num_value(obj,minvalue,maxvalue){   
	var msgDom = $(obj).attr("msgDom");
    var reg = /^[0-9]+$/;   
    if(obj.value!=""&&!reg.test(obj.value)){  
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+'只能输入数字！');
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+'只能输入数字！', modal: true, autoCloseTime: 3000});
		 }
    	
        obj.value = "";   
        obj.focus();   
        return false;   
    }else if(minvalue>obj.value||obj.value>maxvalue){   
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html($(obj).attr("tipName")+"的范围是"+minvalue+"-"+maxvalue+"!");
		 } else {
			 $.dialog({type: "warn", content:$(obj).attr("tipName")+"的范围是"+minvalue+"-"+maxvalue+"!", modal: true, autoCloseTime: 3000});
		 }
    	
        obj.value="";   
        obj.focus();   
        return false;   
    }   
    if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
    return true;   
} 

//获取鼠标的绝对位置 arr数组0:x 1:y
function getXY()
{
	var arr = new Array(2);
    var e = event || window.event;
    var x = mouseX(e);
    var y = mouseY(e);
    arr[0]=x;
    arr[1]=y;
    return arr;
}
//获取鼠标X的绝对位置
function mouseX(evt) {
	if (evt.pageX) return evt.pageX;
	else if (evt.clientX)
	   return evt.clientX + (document.documentElement.scrollLeft ?
	   document.documentElement.scrollLeft :
	   document.body.scrollLeft);
	else return null;
}
//获取鼠标Y的绝对位置
function mouseY(evt) {
	if (evt.pageY) return evt.pageY;
	else if (evt.clientY)
	   return evt.clientY + (document.documentElement.scrollTop ?
	   document.documentElement.scrollTop :
	   document.body.scrollTop);
	else return null;
}
//检测图片 _file:文件控件，type:img或以.xx|.xx， kb:允许文件最大多少KB -1表示无限制
function checkFile(_file,type,kb) {
	var msgDom = $(_file).attr("msgDom");
	/**
	var allowedExtensions = [];
	if(type=="img") {
		allowedExtensions = ['jpg','jpeg','gif','png']
	} else {
		var arrs = type.split("|");
		for(var i=0;i<arr.length;i++) {
			allowedExtensions[i] = arrs[i]
		}
	}
	
    $(_file).checkFileTypeAndSize({
     allowedExtensions: allowedExtensions,
     maxSize: kb,
     success: function(){return true;},
     extensionerror: function(){
    	 $.dialog({type: "warn", content:$(obj).attr("tipName")+'上传格式不对！', modal: true, autoCloseTime: 3000});
    	 return false;},
     sizeerror: function(){
    		 $.dialog({type: "warn", content:$(obj).attr("tipName")+'文件大小不能大于'+kb+'KB！', modal: true, autoCloseTime: 3000});
    		 return false;}
    });
	**/
	
	
	if($(_file).val()=="") {
		 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
			 $("#"+msgDom).html("请选择"+$(_file).attr("tipName")+"!");
		 } else {
			 $.dialog({type: "warn", content:"请选择"+$(_file).attr("tipName")+"!", modal: true, autoCloseTime: 3000});
		 }
		
		return false;
	}
//	var image=new Image();
//    image.dynsrc=_file.value;
	var pre = $(_file).val().substring($(_file).val().lastIndexOf("."),$(_file).val().length);
	
	if(type=="all") {
//		 if(image.fileSize/1024>kb&&kb!="-1"){
//		   $.dialog({type: "warn", content:"文件大小不能大于"+kb+"KB。", modal: true, autoCloseTime: 3000});
//	       clearInputFile(_file);  
//	       return false;
//	   }
	} else {
		if(type=="img") {
			if(!(_file.value.indexOf("jpeg")!=-1||_file.value.indexOf("JPEG")!=-1
					||_file.value.indexOf("jpg")!=-1||_file.value.indexOf("JPG")!=-1
					||_file.value.indexOf("gif")!=-1||_file.value.indexOf("GIF")!=-1
					||_file.value.indexOf("png")!=-1||_file.value.indexOf("PNG")!=-1
					||_file.value.indexOf("jpg")!=-1||_file.value.indexOf("jpg")!=-1)){
				 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
					 $("#"+msgDom).html($(_file).attr("tipName")+"只支持图片格式!");
				 } else {
					 $.dialog({type: "warn", content:$(_file).attr("tipName")+"只支持图片格式!", modal: true, autoCloseTime: 3000});
				 }
						
						clearInputFile(_file);
					    return false; 
					}
//		   if(image.fileSize/1024>kb&&kb!="-1"){
//		       $.dialog({type: "warn", content:"图片大小不能大于"+kb+"KB。", modal: true, autoCloseTime: 3000});
//		       clearInputFile(_file);
//		       return false;
//		   }     
		} else {
			if(type.indexOf(pre)<0) {
				 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
					 $("#"+msgDom).html($(_file).attr("tipName")+"只支持"+type+"格式!");
				 } else {
					 $.dialog({type: "warn", content:$(_file).attr("tipName")+"只支持"+type+"格式!", modal: true, autoCloseTime: 3000});
				 }
				
				clearInputFile(_file);
			    return false; 
			}
//		   if(image.fileSize/1024>kb&&kb!="-1"){
//		       $.dialog({type: "warn", content:"文件大小不能大于"+kb+"KB。", modal: true, autoCloseTime: 3000});
//		       clearInputFile(_file);
//		       return false;
//		   }
		}
		
	}   

	if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
		 $("#"+msgDom).html("");
	 }
   return true;
}
//清除file控件值
function clearInputFile(_file) {
	_file.select();
    document.execCommand('Delete');
}

//自主研发万能FORM验证工具
//把需要验证的input控件中加上 req="true" 如果为false时,则不是必填项。但如果存在值,则进行检验
//并且在input控件中指定值的类型 valType="";
//valType类型分别有:email|number|file|double|string|chinese|website|tel
//其中input类型为File的 必须要控件上加上fileType:img或以.xx|.xx 和fileMaxLen:允许文件最大多少KB -1表示无限制
//当valType为number时 min:为最小数 max为最大数 。不填写为无限制
//填写tipName=""提示名字
//其中如果添加relyOn的属性 意为依赖某某的属性值判断是否需要验证
//relyOn="domId|domAttribute:val1||val2||val3...." 如果依赖id为domId的控件属性domAttribute为val时。可以验证控件的值
//当valType为string时,min max可验证长度
//当valType为string时,且 ccode="true" 为验证-验证码的正确性
//当valType为string时,且valEqualsForDom="domId" 为验证与某dom的值必须相同
//当valType为string时,且check="url" url为服务器验证url地址。服务器输出 1:表示通过其余值是服务器输出的错误信息
/**
 *信息显示默认选择dialog,如需显示到某个DOM节点，则需要写上属性msgDom=""，解析:msgDom为内容展示区域的ID值
 */

function checkFormData(form) {
    
	var urlArg = "?";//把各个值组成url参数队列 当form元素中含有ajaxAction时,此属性有效。
	var inputs = $(form).find("input");
	for(var i=0;i<inputs.length;i++) {
		if($(inputs[i]).attr("req")=="true") {
			var result = checkUnit(inputs[i]); 
			if(result=="continue")continue;  
			else if(result==false) return false;  
			urlArg+=(inputs[i].name+"="+inputs[i].value);
			if(i<(inputs.length-1)) urlArg+="&";
		} else if($(inputs[i]).attr("req")=="false") {
			if($.trim($(inputs[i]).val())!="") {
				var result = checkUnit(inputs[i]);
				if(result=="continue")continue;
				else if(result==false) return false;
				urlArg+=(inputs[i].name+"="+inputs[i].value);
				if(i<(inputs.length-1)) urlArg+="&";
			}
		}
	}
	loadingShowBg();
	$(form).ajaxSubmit({dataType:"json",success:function(data){
		loadingHideBg();
		// $.dialog({type: "warn", content:$(obj).attr("tipName")+"格式不正确！", modal: true, autoCloseTime: 3000});
		if( data.msg!="") {
			$.message({type: data.status, content: data.msg});
		}
		if( data.redirectUri!="") {
			location.href = data.redirectUri
		}
	}});  
	
	return false;
}

function checkUnit(obj) {
	var msgDom = $(obj).attr("msgDom");
			var relyOn = $(obj).attr("relyOn");
			if($.trim(relyOn)!="") { 
				var domid = relyOn.split("|")[0];
				var domAttribute = relyOn.split("|")[1].split(":")[0];
				var domAttributeVal=$("#"+domid).attr(domAttribute);
				
				var vals = relyOn.split("|")[1].split(":")[1].split("||");
				var isok = true;
				for(var i=0;i<vals.length;i++) {
					if(domAttributeVal==vals[i]) {
						isok = false;
						break;
					}
				}
				if(isok==true) return "continue";
				
			}
			if($(obj).attr("valType")=="email") {
				var flag = checkEmail(obj); 
				if(!flag) {
					if($(obj).attr("type")!="hidden") {
						try{obj.focus();}catch(err){}
					} 
					return flag;
				} 
			}  else if($(obj).attr("valType")=="tel") {
				var flag = checkTel(obj);
				if(!flag) {
					if($(obj).attr("type")!="hidden") {
						try{obj.focus();}catch(err){}
					}
					return flag;
				} 
			}else if($(obj).attr("valType")=="number") {
				var flag = checkNumber(obj);
				if(!flag) {
					if($(obj).attr("type")!="hidden") {
						try{obj.focus();}catch(err){}
					}
					return flag;
				} 
			} else if($(obj).attr("valType")=="file") {
				var flag = checkFile(obj,$(obj).attr("fileType"),$(obj).attr("fileMaxLen"));
				if(!flag) { 
					if($(obj).attr("type")!="hidden") {
						try{obj.focus();}catch(err){}
					}
					return flag;
				} 
				
				/***
				 if($("#uploadForm").length==0) {
					var uploadForm = '<form id="uploadForm" style="display:none" action="" enctype="multipart/form-data" method="post"></form>';
					$("body").append(uploadForm);
					} 
					$("#uploadForm").empty();
					$("#uploadForm").append($(obj).clone());
				 */
				
			} else if($(obj).attr("valType")=="double") {
				var flag = checkMoney(obj);
				if(!flag) {
					if($(obj).attr("type")!="hidden") {
						try{obj.focus();}catch(err){}
					}
					return flag;
				} 
			} else if($(obj).attr("valType")=="chinese") {
				var reg=/^[\u0391-\uFFE5]+$/;    
			    if(!reg.test($(obj).val())){  
					 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
						 $("#"+msgDom).html( $(obj).attr("tipName")+'内容必须为中文！');
					 } else {
						 $.dialog({type: "warn", content: $(obj).attr("tipName")+'内容必须为中文！', modal: true, autoCloseTime: 3000});
					 }
			        try{obj.focus();}catch(err){}
			        return false;      
			    }   
			} else if($(obj).attr("valType")=="website") {
				var reg=/^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]':+!]*([^<>\"\"])*$/;
			    if(!reg.test($(obj).val())){  
					 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
						 $("#"+msgDom).html( $(obj).attr("tipName")+'内容必须为中文！');
					 } else {
						 $.dialog({type: "warn", content:$(obj).attr("tipName")+"格式不正确！", modal: true, autoCloseTime: 3000});
					 }
			    	
			        try{obj.focus();}catch(err){} 
			        return false;   
			    }   
			} else if($(obj).attr("valType")=="string") {
				if($.trim($(obj).val())=="") {
					 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
						 $("#"+msgDom).html( $(obj).attr("tipName")+"不能为空!");
					 } else {
						 $.dialog({type: "warn", content: $(obj).attr("tipName")+"不能为空!", modal: true, autoCloseTime: 3000});
					 }
					
					if($(obj).attr("type")!="hidden") {
						try{obj.focus();}catch(err){}
					}
					return false;
				}
				
				if(
						($(obj).attr("min")!=undefined&&$(obj).attr("min")!=""&&$(obj).attr("min")!=null)&&
						($(obj).attr("max")!=undefined&&$(obj).attr("max")!=""&&$(obj).attr("max")!=null)
				) {
					if(check_str_len(obj,parseInt($(obj).attr("min")),parseInt($(obj).attr("max")))==false) return false;
				}
				
				 
			
				if($(obj).attr("ccode")=="true") {
					var txt = $reqJSON.get("/validateResponse?ccode="+$(obj).val(),null);
					if(txt.status!="success") {
						 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
							 $("#"+msgDom).html(txt.msg);
						 } else {
							 //$.dialog({type: "warn", content: txt.msg, modal: true, autoCloseTime: 3000});
								$.message({type: txt.status, content: txt.msg});
						 }
						
						try{obj.focus();}catch(err){}
						return false;
					}
				}
				if($(obj).attr("valEqualsForDom")!=undefined&&$(obj).attr("valEqualsForDom")!=""&&$(obj).attr("valEqualsForDom")!=null) {
					if($(obj).val()!=$("#"+$(obj).attr("valEqualsForDom")).val()) {
						 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
							 $("#"+msgDom).html($(obj).attr("tipName")+"与\""+$("#"+$(obj).attr("valEqualsForDom")).attr("tipName")+"\"不相同!");
						 } else {
							 $.dialog({type: "warn", content: $(obj).attr("tipName")+"与\""+$("#"+$(obj).attr("valEqualsForDom")).attr("tipName")+"\"不相同!", modal: true, autoCloseTime: 3000});
						 }
						
						try{obj.focus();}catch(err){}
						return false;
					}
				}
				if($(obj).attr("check")!=undefined&&$(obj).attr("check")!=""&&$(obj).attr("check")!=null) {
					var txt = $reqTxt.get($(obj).attr("check")+obj.value,null);
					if(txt!="1") {
						 if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
							 $("#"+msgDom).html(txt);
						 } else {
							 $.dialog({type: "warn", content: txt, modal: true, autoCloseTime: 3000});
						 }
						
						try{obj.focus();}catch(err){}
						return false;
					}
				}
			}   
			
			if(null!=msgDom&&msgDom!=""&&msgDom!=undefined) {
				 $("#"+msgDom).html("");
			 }
			return true;
}


//跳转本地同时添加或更改参数值
function goLocationWithParam(paramNames,vals) {
	
	
	
	var pns = eval(paramNames);
	var vs = eval(vals);
	var url = location.href;
	
	if(url.indexOf("?")>0) { 
		for(var i=0;i<pns.length;i++) {
			if(url.indexOf(pns[i]+"=")>0) {
				var str = url.substring(url.indexOf(pns[i]+"="),url.length)
				if(str.indexOf("&")>0) {
					
					var temp = str.substring(0,str.indexOf("&"));
					str = str.replace(temp,"");
					temp = pns[i]+"="+vs[i];
					str = temp+str;
					url = url.substring(0,url.indexOf(pns[i]+"="))+str;
					
				} else {
					var newurl = url.substring(0,url.indexOf(str));
					newurl+=(pns[i]+"="+vs[i]);
					url = newurl;
				}
			} else {
				url+=("&"+pns[i]+"="+vs[i]);
			}
		}
	} else {
		for(var i=0;i<pns.length;i++) {
			if(i==0) url+="?";
			url+=(pns[i]+"="+vs[i]);
			if(i<(pns.length-1)) url+="&";
		}
	}
	location.href=url;
}



// 解决IE6不缓存背景图片问题
if(!window.XMLHttpRequest) {
	document.execCommand("BackgroundImageCache", false, true);
}

// 添加收藏夹
function addFavorite(url, title) {
	if (document.all) {
		window.external.addFavorite(url, title);
	} else if (window.sidebar) {
		window.sidebar.addPanel(title, url, "");
	}
}

// html字符串转义
function htmlEscape(htmlString) {
	htmlString = htmlString.replace(/&/g, '&amp;');
	htmlString = htmlString.replace(/</g, '&lt;');
	htmlString = htmlString.replace(/>/g, '&gt;');
	htmlString = htmlString.replace(/'/g, '&acute;');
	htmlString = htmlString.replace(/"/g, '&quot;');
	htmlString = htmlString.replace(/\|/g, '&brvbar;');
	return htmlString;
}

// 设置Cookie
function setCookie(name, value) {
	var expires = (arguments.length > 2) ? arguments[2] : null;
	document.cookie = name + "=" + encodeURIComponent(value) + ((expires == null) ? "" : ("; expires=" + expires.toGMTString())) + ";path=" + shopxx.base;
}

// 获取Cookie
function getCookie(name) {
	var value = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
	if (value != null) {
		return decodeURIComponent(value[2]);
    } else {
		return null;
	}
}

// 删除cookie
function removeCookie(name) {
	var expires = new Date();
	expires.setTime(expires.getTime() - 1000 * 60);
	setCookie(name, "", expires);
}

// 浮点数加法运算
function floatAdd(arg1, arg2) {
	var r1, r2, m;
	try{
		r1 = arg1.toString().split(".")[1].length;
	} catch(e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch(e) {
		r2 = 0;
	}
	m = Math.pow(10, Math.max(r1, r2));
	return (arg1 * m + arg2 * m) / m;
}

// 浮点数减法运算
function floatSub(arg1, arg2) {
	var r1, r2, m, n;
	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch(e) {
		r1 = 0
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch(e) {
		r2 = 0
	}
	m = Math.pow(10, Math.max(r1, r2));
	n = (r1 >= r2) ? r1 : r2;
	return ((arg1 * m - arg2 * m) / m).toFixed(n);
}

// 浮点数乘法运算
function floatMul(arg1, arg2) {
	var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	} catch(e) {}
	try {
		m += s2.split(".")[1].length;
	} catch(e) {}
	return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}

// 浮点数除法运算
function floatDiv(arg1, arg2) {
	var t1 = 0, t2 = 0, r1, r2;    
	try {
		t1 = arg1.toString().split(".")[1].length;
	} catch(e) {}
	try {
		t2 = arg2.toString().split(".")[1].length;
	} catch(e) {}
	r1 = Number(arg1.toString().replace(".", ""));
	r2 = Number(arg2.toString().replace(".", ""));
	return (r1 / r2) * pow(10, t2 - t1);
}

// 设置数值精度
function setScale(value, scale, roundingMode) {
	if (roundingMode.toLowerCase() == "roundhalfup") {
		return (Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale)).toFixed(scale);
	} else if (roundingMode.toLowerCase() == "roundup") {
		return (Math.ceil(value * Math.pow(10, scale)) / Math.pow(10, scale)).toFixed(scale);
	} else {
		return (Math.floor(value * Math.pow(10, scale)) / Math.pow(10, scale)).toFixed(scale);
	}
}

function openWin(_TITLE,_ID,_URL){
	parent.mainFrame.openWin(_TITLE,_ID,_URL);
}

$().ready( function() {
	
	$(".showtrue a").click(function(){
		var tab_name = $(this).attr("tab_name");
		var tab_id = $(this).attr("tab_id");
		var tab_url = $(this).attr("tab_url");
		openWin(tab_name,tab_id,tab_url);
		
	});
	
	var $body;
	var dialogIdNumber = 0;
	var dialogZIndex = 100;
	var messageIdNumber = 0;
	
	$.dialog = function (settings) {
		
		var dialogId;
		
		if (settings.id != null) {
			dialogId = settings.id;
		} else {
			dialogId = "dialog" + dialogIdNumber;
			dialogIdNumber ++;
		}
		if (settings.content == null) {
			settings.content = "";
		}
		if (settings.width == null || settings.width == "auto") {
			settings.width = 320;
		}
		if (settings.height == null) {
			settings.height = "auto";
		}
		
		if ($body == null) {
			$body = $("body");
		}
		
		var dialogHtml = "";
		if(!settings.dialogContentStyle) {
			settings.dialogContentStyle = "";
		} 
		
		if (settings.modal == true) {
			dialogHtml += '<div id="dialogOverlay' + dialogId + '" class="dialogOverlay"></div>';
		}
		
		if (settings.className != null) {
			dialogHtml += '<div style="top:15%" id="' + dialogId + '" class="baseDialog ' + settings.className + '"><div class="dialogWrap"></div><div class="dialogMain">';
		} else {
			dialogHtml += '<div style="top:15%" id="' + dialogId + '" class="baseDialog"><div class="dialogWrap"></div><div class="dialogMain">';
		}
		
		if (settings.title != null) {
			dialogHtml += '<div id="dialogTitle' + dialogId + '" class="dialogTitle">' + settings.title + '</div><div id="dialogClose' + dialogId + '" class="dialogClose"></div>';
		} else {
			dialogHtml += '<div id="dialogClose' + dialogId + '" class="dialogClose"></div>';
		}
		
		if (settings.type != null) {
			dialogHtml += '<div style="'+settings.dialogContentStyle+'" id="dialogContent' + dialogId + '" class="dialogContent dialog' + settings.type + 'Icon">' + settings.content + '</div>';
		} else {
			dialogHtml += '<div style="'+settings.dialogContentStyle+'" id="dialogContent' + dialogId + '" class="dialogContent">' + settings.content + '</div>';
			
		}
		
		if (settings.ok != null || settings.cancel != null) {
			dialogHtml += '<div id="dialogButtonArea' + dialogId + '" class="dialogButtonArea">';
		}
		
		if (settings.ok != null) {
			dialogHtml += '<input type="button" id="dialogOk' + dialogId + '" class="formButton" value="' + settings.ok + '" hidefocus="true" />';
		}
		
		if (settings.cancel != null) {
			dialogHtml += '<input type="button" id="dialogCancel' + dialogId + '" class="formButton" value="' + settings.cancel + '" hidefocus="true" />';
		}
		
		if (settings.ok != null || settings.cancel != null) {
			dialogHtml += '</div>';
		}
		
		if(!window.XMLHttpRequest) {
			dialogHtml += '<iframe id="dialogIframe' + dialogId + '" class="dialogIframe"></iframe>';
		}
		
		dialogHtml += '</div></div>';
		
		$body.append(dialogHtml);
		
		var dialogX;
		var dialogY;
		
		var $dialogOverlay = $("#dialogOverlay" + dialogId);
		var $dialog = $("#" + dialogId);
		var $dialogTitle = $("#dialogTitle" + dialogId);
		var $dialogClose = $("#dialogClose" + dialogId);
		var $dialogOk = $("#dialogOk" + dialogId);
		var $dialogCancel = $("#dialogCancel" + dialogId);
		
		$dialog.css({"width": settings.width, "height": settings.height, "margin-left": - parseInt(settings.width / 2), "z-index": dialogZIndex ++});
		
		if(!window.XMLHttpRequest) {
			var $dialogIframe = $("#dialogIframe" + dialogId);
			$dialogIframe.css({"width": $dialog.width() + 20, "height": $dialog.height() + 20});
		}
		
		function dialogClose() {
			$dialogOverlay.remove();
			$dialog.remove();
		}
		
		if (settings.autoCloseTime != null) {
			setTimeout(dialogClose, settings.autoCloseTime);
		}
		
		$dialogClose.click( function() {
			if ($.isFunction(settings.cancelCallback)) {
				if (settings.cancelCallback.apply() != false) {
					dialogClose();
				}
			} else {
				dialogClose();
			}
		});
		
		$dialogOk.click( function() {
			if ($.isFunction(settings.okCallback)) {
				if (settings.okCallback.apply() != false) {
					dialogClose();
				}
			} else {
				dialogClose();
			}
		});
		
		$dialogCancel.click( function() {
			if ($.isFunction(settings.cancelCallback)) {
				if (settings.cancelCallback.apply() != false) {
					dialogClose();
				}
			} else {
				dialogClose();
			}
		});
		
		$dialogTitle.mousedown(function (event) {
			$dialog.css({"z-index": dialogZIndex ++});
			var offset = $(this).offset();
			if(!window.XMLHttpRequest) {
				dialogX = event.clientX - offset.left + 6;
				dialogY = event.clientY - offset.top + 6;
			} else {
				dialogX = event.pageX - offset.left + 6;
				dialogY = event.pageY - offset.top + 6;
			}
			
			$(document).bind("mousemove", function(event) {
				$dialog.css({"top": event.clientY - dialogY, "left": event.clientX - dialogX, "margin": 0});
			});
			return false;
		});
		
		$(document).mouseup(function() {
			$(document).unbind("mousemove");
		});
		/**
		$dialog.keypress(function(event) {
			if(event.keyCode == 13) {
				if ($.isFunction(settings.okCallback)) {
					if (settings.okCallback.apply() != false) {
						dialogClose();
					}
				} else {
					dialogClose();
				}
			}  
		});
		**/
		$dialogOverlay.show();
		$dialog.show();
		$dialog.focus();
		
		return dialogId;
	}
	
	$.closeDialog = function (dialogId) {
		var $dialogOverlay = $("#dialogOverlay" + dialogId);
		var $dialog = $("#" + dialogId);
		
		$dialogOverlay.remove();
		$dialog.remove();
	}
	
	$.message = function (settings) {
	
		if (settings.content == null) {
			settings.content = "";
		}
		
		if ($body == null) {
			$body = $("body");
		}
		
		var messageId = "message" + messageIdNumber;
		messageIdNumber ++;
		
		var messageHtml;
		
		if (settings.type != null) {
			messageHtml = '<div id="' + messageId + '" class="baseMessage"><div class="messageContent message' + settings.type + 'Icon">' + settings.content + '</div></div>';
		} else {
			messageHtml = '<div id="' + messageId + '" class="baseMessage"><div class="messageContent">' + settings.content + '</div></div>';
		}
		
		$body.append(messageHtml);
		
		var $message = $("#" + messageId);
		
		$message.css({"margin-left": "-" + parseInt($message.width() / 2) + "px"}).show();
		
		var time = 2000;
		if (settings.time != null) {
			time = settings.time;
		}
		setTimeout(function() {
			$message.animate({left: 0, opacity: "hide"}, "slow", function() {
				$message.remove();
			});
		}, time);     
		
		return messageId;
	}
	

});
function reloadPager(json) {
	var page = '<div class="pager"><span id="pager"><ul class="pages">';
	if(json.pageCount==1) {
		page+=('<li class="pgNext pgEmpty">首页</li>');
	} else {
	   	page+=('<li class="pgNext"><a href="javascript:;" onclick="loadData(1)">首页</a></li>');	   
	}
	   
	if(json.pageNumber <= 1) {
	   page+=('<li class="pgNext pgEmpty">上一页</li>');
	} else {
	   page+=('<li class="pgNext"><a href="javascript:;" onclick="loadData('+(json.pageNumber-1)+')">上一页</a></li>');	   
	}
	   	
	for(var p = json.pageNumber-5;p<=json.pageNumber+5;p++) {
		    if(p > 0 && p <= json.pageCount) {
				page+=('<li class="page-number '+(p==json.pageNumber?"pgCurrent":"")+'"><a href="javascript:;" onclick="loadData('+p+')" >'+p+'</a></li>');
			}
	}
		
	if(json.pageNumber >= json.pageCount) {
		page+=('<li class="pgNext pgEmpty">下一页</li>');
	} else {
		page+=('<li class="pgNext"><a href="javascript:;" onclick="loadData('+(json.pageNumber+1)+')" >下一页</a></li>');	   
	}
		
	if(json.pageCount==1) {
	   page+=('<li class="pgNext pgEmpty">末页</li>');
	} else {
	   page+=('<li class="pgNext"><a href="javascript:;" onclick="loadData('+json.pageCount+')" >末页</a></li>');	   
	}
		
	page+=('</ul></span></div><div></div>');
	$(".pagerBar .pager").remove();  
	$(".pagerBar").append(page);	
	
}   