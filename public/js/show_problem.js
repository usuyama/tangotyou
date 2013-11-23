$(document).ready(function() {
  answered = false;
  function answer(option) {
    if (answered == true) {
      return;
    }
    answered = true;
    $(".answer").show();
    switch(option) {
      case "true":
        $(".false").removeClass("button-flat-caution");
      $(".unknown").removeClass("button-flat-highlight");
      if($(".true").hasClass("true-answer") == true) {
        $(".correct").show();
        send_answer(1, 1);
      } else {
        $(".incorrect").show();
        send_answer(1, 0);
      }
      break;
      case "false":
        $(".true").removeClass("button-flat-primary");
      $(".unknown").removeClass("button-flat-highlight");
      if($(".false").hasClass("true-answer") == true) {
        $(".correct").show();
        send_answer(0, 1);
      } else {
        $(".incorrect").show();
        send_answer(0, 0);
      }
      break;
      case "unknown":
        $(".true").removeClass("button-flat-primary");
      $(".false").removeClass("button-flat-caution");
      send_answer(2, 2);
      break;
    }
  }
  function send_answer(user_response, correctness) {
    problem_id = $("#problem_id").val();
    $.post("/practice/answer", { problem_id: problem_id, user_response: user_response, correctness: correctness}, function(data) {
      var numProblemsForNext = data;
      if($(".correct").is(":visible") == false) {
        $("#notify_review").text(data + "問後に、復習します!").show();
      }
    });
  }
  $(".true").click(function() { answer("true"); });
  $(".false").click(function() { answer("false"); });
  $(".unknown").click(function() { answer("unknown"); });
});
