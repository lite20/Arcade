@get "./sys.arc" as sys

@def int[1 flag, * val]

init:                 \ initx:
mov "compute" eax     \ mov 1 eax
@eval(4 + 2, eax) ebx \ mov "campoting" eax
mov 2 ebx             \ mov 2 ebx
call sys_log          \ call sys_log
add eax ebx           \ add eax ebx
call sys_log          \ call sys_log

[] main:
call init \ call initx
