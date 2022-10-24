<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="foot">
    <div class="wave-box">
        <div class="marquee-box " id="marquee-box">
            <div class="marquee">
                <div class="wave-list-box" id="wave-list-box1">
                    <ul>
                        <li><img height="60" alt="波浪" src="${staticPath}/img/wave_02.png"></li>
                    </ul>
                </div>
                <div class="wave-list-box" id="wave-list-box2">
                    <ul>
                        <li><img height="60" alt="波浪" src="${staticPath}/img/wave_02.png"></li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="marquee-box" id="marquee-box3">
            <div class="marquee">
                <div class="wave-list-box" id="wave-list-box4">
                    <ul>
                        <li><img height="60" alt="波浪" src="${staticPath}/img/wave_01.png"></li>
                    </ul>
                </div>
                <div class="wave-list-box" id="wave-list-box5">
                    <ul>
                        <li><img height="60" alt="波浪" src="${staticPath}/img/wave_01.png"></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div>© 版权所有 中国移动通信集团有限公司安徽分公司</div>
</div>
<script type="text/javascript">
//波浪动画
$(function () {
var marqueeScroll = function (id1, id2, id3, timer) {
    var $parent = $("#" + id1);
    var $goal = $("#" + id2);
    var $closegoal = $("#" + id3);
    $closegoal.html($goal.html());
    function Marquee() {
        if (parseInt($parent.scrollLeft()) - $closegoal.width() >= 0) {
            $parent.scrollLeft(parseInt($parent.scrollLeft()) - $goal.width());
        }
        else {
            $parent.scrollLeft($parent.scrollLeft() + 1);
        }
    }

    setInterval(Marquee, timer);
}
var marqueeScroll1 = new marqueeScroll("marquee-box", "wave-list-box1", "wave-list-box2", 20);
var marqueeScroll2 = new marqueeScroll("marquee-box3", "wave-list-box4", "wave-list-box5", 40);
});
</script>