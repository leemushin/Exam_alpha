		function startClock(wgt) {
			var countDownDate = new Date().getTime()+3600000;//設定倒數時間為現在時間+1小時
			var updateClock = function() {				
				var now = new Date();//取得現在時間
				var distance = countDownDate - now;//倒數時間-現在時間

					var gethours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));//取得小時
    				var getminutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));//取得分
    				var getseconds = Math.floor((distance % (1000 * 60)) / 1000);//取得秒
    				//如果大於0就顯示秒數,如果小於0顯示0:0:0(避免小lag造成顯示負數)
    				if(distance >0){
						wgt.$f('hour').$n().textContent = gethours;
						wgt.$f('min').$n().textContent = getminutes;
						wgt.$f('sec').$n().textContent = getseconds;
					}
					else{
						wgt.$f('hour').$n().textContent = "0";
						wgt.$f('min').$n().textContent = "0";
						wgt.$f('sec').$n().textContent = "0";
					}

			}
			wgt.clockId=setInterval(updateClock, 1000);//每一秒更新一次
			updateClock();
		}