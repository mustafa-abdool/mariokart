function findBottom (y : int) : int
    for yy : y .. maxy
	for xx : 0 .. maxx
	    if whatdotcolour (xx, yy) > 0 then
		result yy
	    end if
	end for
    end for
    result - 1
end findBottom

function findTop (y : int) : int
var clear:=false    
    for yy : y .. maxy
	clear:=true
	for xx : 0 .. maxx
	    if whatdotcolour (xx, yy) > 0 then
		clear := false
	    end if
	end for
	if clear then
	    result yy
	end if
    end for
    result - 1
end findTop

function findLeft (x, y1, y2 : int) : int
    for xx : x .. maxx
	for yy : y1 .. y2
	    if whatdotcolour (xx, yy) > 0 then
		result xx               
	    end if
	end for
    end for
    result - 1
end findLeft


function findRight (x, y1, y2 : int) : int
    var clear:boolean
    for xx : x .. maxx
	clear := true
	for yy : y1 .. y2
	    if whatdotcolour (xx, yy) > 0 then
		clear :=false               
	    end if
	end for
	if clear then
	    result xx
	end if
    end for
    result - 1
end findRight


const PIC := "koopa troopa"
var picID : int := Pic.FileNew (PIC + ".bmp")
setscreen ("graphics:" + intstr (Pic.Width (picID)) + "," + intstr (Pic.Height (picID)))
Pic.Draw (picID, 0, 0, picCopy)
var picNum := 1
var x,y,x2,y2:int:=0
loop
    y:=findBottom(y2+1)
    y2:=findTop(y)
    exit when y2 = -1
    x:=0
    x2:=0
    loop
	x:=findLeft(x2+1,y,y2)
	exit when x=-1
	x2:=findRight(x,y,y2)
	exit when x2=-1
	Pic.ScreenSave(x-1,y-1,x2+1,y2+1,PIC + intstr(picNum) + ".bmp")
%         drawbox(x,y,x2,y2,2)
%         delay(1000)
	picNum+=1
    end loop
    y:=y2
end loop

