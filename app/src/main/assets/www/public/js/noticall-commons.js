var Noticall={};
Noticall.commons = {
	init: function() {
		this.DashEvent();
		this.clickSend();
		this.NewsEvent();
		this.callEvent();
		this.clickTTS();
	},


	DashEvent: function(){

		function getBaseURL () {
			return location.protocol + "//" + location.hostname +
				(location.port && ":" + location.port) ;
		};
		$.ajax({
			url:getBaseURL()+'/api/getCallLog/usage',
			dataType:"json",
			success:function(result)
			{
				console.log("DashBoard:"+result);
				console.log("DashBoard:"+result.status);
				console.log("DashBoard:"+result.responseData.remainCallDuration);
				$("#callNumber").html(result.responseData.remainCallDuration+"분");
				$("#newNumber").html(result.responseData.totalCallDuration+"분");
			},
			error : function()
			{
				alert(url);
				alert("error Dash");
			}
		});

	},

	NewsEvent: function() {
		function getBaseURL () {
			return location.protocol + "//" + location.hostname +
				(location.port && ":" + location.port) ;
		};

		$.ajax({
			url:getBaseURL()+'/api/getNews?limit=20',
			dataType: "json",
			success: function (result) {
				for (var i = 0;  i<20 ; i=i+4) {
					$("#newsnews").append(
						"<div class= 'alert alert-success'>" + "[" + result.responseData.array[i].date + "] " + result.responseData.array[i].contents + "  <a href =" + "'" + result.responseData.array[i].link + "'>" + "기사 원문" + "</a>"+"</div>"
					);
					$("#newsnews").append(
						"<div class= 'alert alert-info'>" + "[" + result.responseData.array[i+1].date + "] " + result.responseData.array[i+1].contents + "  <a href =" + "'" + result.responseData.array[i+1].link + "'>" + "기사 원문" + "</a>"+"</div>"
					);
					$("#newsnews").append(
						"<div class= 'alert alert-warning'>" + "[" + result.responseData.array[i+2].date + "] " + result.responseData.array[i+2].contents + "  <a href =" + "'" + result.responseData.array[i+2].link + "'>" + "기사 원문" + "</a>"+"</div>"
					);
					$("#newsnews").append(
						"<div class= 'alert alert-danger'>" + "[" + result.responseData.array[i+3].date + "] " + result.responseData.array[i+3].contents + "  <a href =" + "'" + result.responseData.array[i+3].link + "'>" + "기사 원문" + "</a>"+"</div>"
					);
				}
			},
			error: function () {
				alert("error News");
			}
		});
	},

	callEvent: function(){
		function getBaseURL () {
			return location.protocol + "//" + location.hostname +
				(location.port && ":" + location.port) ;
		};

		$('table').ready(function(){

			$.ajax({
				url:getBaseURL()+'/api/getCallLog/history',
				dataType:"json",
				success:function(result)
				{
					var arrayLen=result.responseData.length;
					console.log("CallHistory:"+result);
					console.log("CallHistory:"+result.status);
					console.log("CallHistory:"+result.responseData[0].date);
					console.log("CallHistory:"+result.responseData[0].name);
					console.log("CallHistory:"+"길이는"+arrayLen);
					var j=1;
					for(var i = 0 ; i < 30 ; i= i+4 ){
						$(".kkk").append(
							"<tr class='success'>"+"<td>"+j+"</td>"+"<td>"+result.responseData[i].date+"</td>"+"<td>"+result.responseData[i].name+"</td>"+"<td>"+parseInt((result.responseData[i].duration)/60)+"분 "+parseInt((result.responseData[i].duration)%60)+"초"+"</td>"+"</tr>");
						j++;
						$(".kkk").append(
							"<tr class='info'>"+"<td>"+j+"</td>"+"<td>"+result.responseData[i+1].date+"</td>"+"<td>"+result.responseData[i+1].name+"</td>"+"<td>"+parseInt((result.responseData[i+1].duration)/60)+"분 "+parseInt((result.responseData[i+1].duration)%60)+"초"+"</td>"+"</tr>");
						j++;
						$(".kkk").append(
							"<tr class='warning'>"+"<td>"+j+"</td>"+"<td>"+result.responseData[i+2].date+"</td>"+"<td>"+result.responseData[i+2].name+"</td>"+"<td>"+parseInt((result.responseData[i+2].duration)/60)+"분 "+parseInt((result.responseData[i+2].duration)%60)+"초"+"</td>"+"</tr>");
						j++;
						$(".kkk").append(
							"<tr class='danger'>"+"<td>"+j+"</td>"+"<td>"+result.responseData[i+3].date+"</td>"+"<td>"+result.responseData[i+3].name+"</td>"+"<td>"+parseInt((result.responseData[i+3].duration)/60)+"분 "+parseInt((result.responseData[i+3].duration)%60)+"초"+"</td>"+"</tr>");
						j++;
					}

				},
				error : function()
				{
					alert("error History");
				}
			});
			return false;
		});

		$.ajax({
			url:getBaseURL()+'/api/getCallLog/rank',
			dataType:"json",
			success:function(result)
			{


				$(".jjj").append(
					"<tr>"+"<td>"+"<h4>"+"1위"+"</h4>"+"</td>" + "<td>" +"<h4>"+ result.responseData[0].name +"</h4>"+ "</td>" + "<td>" +"<h4>"+ parseInt((result.responseData[0].duration)/60)+"분 "+parseInt((result.responseData[0].duration)%60)+"초" +"</h4>"+ "</td>" + "<td>" + "<h4>"+ result.responseData[0].count + "회" +"</h4>"+ "</td>"+"</tr>"
				);
				$(".jjj").append(
					"<tr>"+"<td>"+"<h5>"+result.responseData[1].rankDuration +"위"+"</h5>"+"</td>" + "<td>" +"<h5>"+ result.responseData[1].name +"</h5>"+ "</td>" + "<td>" +"<h5>"+ parseInt((result.responseData[1].duration)/60)+"분 "+parseInt((result.responseData[1].duration)%60)+"초" +"</h5>"+ "</td>" + "<td>" + "<h5>"+ result.responseData[1].count + "회" +"</h5>"+ "</td>"+"</tr>"
				);
				$(".jjj").append(
					"<tr>"+"<td>"+"<h5>"+result.responseData[2].rankDuration +"위"+"</h5>"+"</td>" + "<td>" +"<h5>"+ result.responseData[2].name +"</h5>"+ "</td>" + "<td>" +"<h5>"+ parseInt((result.responseData[2].duration)/60)+"분 "+parseInt((result.responseData[2].duration)%60)+"초" +"</h5>"+ "</td>" + "<td>" + "<h5>"+ result.responseData[2].count + "회" +"</h5>"+ "</td>"+"</tr>"
				);
				$(".jjj").append(
					"<tr>"+"<td>"+result.responseData[3].rankDuration +"위"+"</td>" + "<td>"+ result.responseData[3].name +"</td>" + "<td>"+ parseInt((result.responseData[3].duration)/60)+"분 "+parseInt((result.responseData[3].duration)%60)+"초"+ "</td>" + "<td>"+ result.responseData[3].count + "회"+ "</td>"+"</tr>"
				);

				$(".jjj").append(
					"<tr>"+"<td>"+result.responseData[4].rankDuration +"위"+"</td>" + "<td>"+ result.responseData[4].name +"</td>" + "<td>"+ parseInt((result.responseData[4].duration)/60)+"분 "+parseInt((result.responseData[4].duration)%60)+"초"+ "</td>" + "<td>"+ result.responseData[4].count + "회"+ "</td>"+"</tr>"
				);


			},
			error : function()
			{
				alert("error Rank");
			}
		});

	},
	clickSend: function() {
		function getBaseURL () {
			return location.protocol + "//" + location.hostname +
				(location.port && ":" + location.port) ;
		};
		
		$('#userText').ready(function() {
		$.ajax({
			url:getBaseURL()+'/api/userTTS',
			dataType:"json",
			success:function(result)
			{
				console.log("유저 텍스트 성공");
				for(var i = 0 ; i < result.responseData.array.length ; i++){
					$('#userText').append(result.responseData.array[i].contents+"\n");
				}
			},
			error : function()
			{
				alert("error userTTS ");
			}
		});
	});
		$('#sndMsg').click(function() {
			var usertext = $('#userText').val();
			console.log(usertext);
			var str = usertext.replace(/(\r\n|\n|\\n)/gi,"\\n");
			
				$.post("/api/userTTS",
				{
					contents : str,
					listNumber : 0
				})
				.done(function() {
					alert( "설정 완료" );
				})
				.fail(function() {
				    alert( "설정 실패" );
				});
			
		});
	},
	clickTTS: function(){
		$('#ttsBtg').click(function(){
			var usertext = $('#userText').val();
			window.open("http://translate.google.com/translate_tts?tl=ko&q="+usertext,"TTSWINDOW","width=200,height=100")
		});
	},
	getToken : function(){
		$.ajax({
			url:'http://'+window.location.host+'/tools/oauth2/getAccessToken',
			success:function(result)
			{
				var JSON_result = JSON.parse(result);
				var response = JSON_result.response;

			},
			error : function()
			{
				alert("error");
			}
		});
	}
};
$(document).ready(function() {
	Noticall.commons.init();
});
