#singleinstance force
CoordMode, Pixel
CoordMode, Mouse

~z::
while (1=1)
{
	
	ImageSearch, fX, fY, 0, 0, 1280, 1024, *5 arenaConfirm.bmp
	if (ErrorLevel == 0){
		Click %fX%, %fY%
	}


	ImageSearch, fX, fY, 0, 0, 1280, 1024, *5 arenaLeave.bmp
	if (ErrorLevel == 0){
		Click %fX%, %fY%
	}


}
return

+^!Esc::ExitApp