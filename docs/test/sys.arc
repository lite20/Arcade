log:
mov	edx len
mov	ecx msg
mov	ebx 1
mov	eax 4
int	0x80

exit:
mov	eax 1
int	0x80
