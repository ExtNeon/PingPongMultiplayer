# Игра "Ping Pong Multiplayer"
## Описание игры
Самая древняя компьютерная игра в мире, но на android. Ваша задача - отбивать мяч, летящий в вашу сторону при помощи небольшой платформы, представляющей ракетку. Раунд игры будет продолжаться до тех пор, пока шар не улетит за пределы игрового поля в определённую сторону.
## Геймплей
Ваша платформа управляется при помощи касания. Она будет следовать за ним по горизонтали, оставаясь неподвижной по вертикали.
Платформа противника может управляться как искуственным интеллектом, так и другим человеком, позволяя организовать игру через Bluetooth.
Шар будет начинать движение в сторону платформы проигравшего последний раунд. В начале игры, шарик будет всегда лететь к организатору игры.
Скорость шара в начале выбирается случайно в зависимости от выбранной максимальной скорости в настройках матча. По мере течения раунда, скорость шара будет постепенно возрастать до тех пор, пока не достигнет максимальной.
Матч будет продолжаться до тех пор, пока счёт одного из игроков не достигнет заданной в настройках организатора матча.
В режиме одиночной игры, скорость платформы соперника будет зависеть от настроек игрока.
Если вам надоело играть, но вы хотите посмотреть на устрашающий бой двух ИИ, вы можете в настройках одиночной игры выбрать пункт "Разрешить переход в авторежим". При этом, если вы во время матча не прикасаетесь к экрану определённое время, ваша платформа переходит в авторежим, и начинает двигаться автоматически. Но стоит вам вновь коснуться экрана - авторежим выключается, и вы снова можете играть самостоятельно.
## Что реализовано на данный момент
На данный момент, полностью реализован режим одиночной игры. 
## Что планируется реализовать в ближайшее время
* Звуковое сопровождение
* Многопользовательский режим
