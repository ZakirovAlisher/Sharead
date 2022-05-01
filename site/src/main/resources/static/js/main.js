var stompClient = null;
var notificationCount = 0;

$(document).ready(function() {
    console.log("Index page is ready");
    connect();

    $("#send").click(function() {
        sendMessage();
    });

    $("#send-private").click(function() {
        sendPrivateMessage();
    });

    $("#cancel").click(function() {
        sendCancelMessage();
    });

    $("#confirm").click(function() {
        sendConfirmMessage();
    });

    $("#notifications").click(function() {
        resetNotificationCount();
    });
});

function connect() {
    var socket = new SockJS('/our-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        updateNotificationDisplay();
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe('/user/topic/private-messages', function (message) {
            showMessage(JSON.parse(message.body).content );
        });

        stompClient.subscribe('/topic/global-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });

        stompClient.subscribe('/user/topic/private-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });
    });
}

function showMessage(message) {
    $("#messages").append("<p>" + message + " </p>");
}

function sendMessage() {
    console.log("sending message");
    stompClient.send("/ws/message", {}, JSON.stringify({'messageContent': $("#message").val()}));
}

function sendPrivateMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message"  , {}, JSON.stringify({'messageContent': $("#private-message").val()
    , 'opponent' : $("#opponent").val(), 'exchangeId' : $("#exchange_id").val(), 'me' : $("#me").val()
    }));
}

function sendCancelMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message"  , {}, JSON.stringify({'messageContent': "<b class='text-danger'>Your opponent canceled offer</b>"
        , 'opponent' : $("#opponent").val(), 'exchangeId' : $("#exchange_id").val(), 'me' : $("#me").val()
    }));
}

function sendConfirmMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message"  , {}, JSON.stringify({'messageContent': "<b class='text-success'>Your opponent confirms exchange. Was the exchange successful? Confirm or cancel</b>"
        , 'opponent' : $("#opponent").val(), 'exchangeId' : $("#exchange_id").val(), 'me' : $("#me").val()
    }));
}

function updateNotificationDisplay() {
    if (notificationCount == 0) {
        $('#notifications').hide();
    } else {
        $('#notifications').show();
        $('#notifications').text(notificationCount);
    }
}

function resetNotificationCount() {
    notificationCount = 0;
    updateNotificationDisplay();
}