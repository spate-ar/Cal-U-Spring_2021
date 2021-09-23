COMMENT!
		Assembly Program 2 - RPN CALCULATOR
		CSC-323: Assembly Language Programming
								  Group# 2
		Andrew Spate		&	Nicholas Spudich		&   Benjamin Reynolds
		spa3195@calu.edu	&	spu8504@calu.edu		&   rey3050@calu.edu
!
INCLUDE Irvine32.inc ; includes library for input-output and string handling


.data

	BUFFER BYTE 15 DUP (0) ; create input buffer
	BYTECOUNT DWORD 8 ; set char count
	STACKSIZE EQU 8	 ; size of stack
	STACKINDEX SDWORD -4; 
	NUMSTACK SDWORD STACKSIZE DUP (0)
	TOPMSG BYTE "Top of stack:     ", 0
	PROMPT BYTE "Enter a positive or negative integer, operator (+, -, *, /), X (Exchange), N (Negate), U (Roll up),  D (Roll Down), V (View), C (Clear), Q (Quit) .", 0
	INVALIDCMD BYTE "Error Message", 0
	INSUFFOP BYTE "Insufficient operand error", 0
	TEXIT BYTE "Terminal powering off... Blowing up in 3, 2, 1....KABOOM!!!", 0
	FULL BYTE "Stack full", 0
	EMPTY BYTE "Stack empty", 0
	FLAG EQU 0
	FALSE equ 0
	FTRUE equ 255
	NULL equ 0
	TAB equ 9
	NUM BYTE ?

.code
		main PROC
		Main_Loop:
						MOV EDX, OFFSET PROMPT	; moves prompt to edx
						CALL WRITESTRING		; writes prompt
						CALL CRLF				; terimates line

		INP:			MOV EDX, OFFSET BUFFER	; loads buffer
						MOV ECX, SIZEOF BUFFER	; gets size
						CALL READSTRING			; gets input from user
						MOV BYTECOUNT, EAX		; number of characters
						

						MOV EDI, 0 ; initialize register EDI to 0

		SKIP:			CMP EDI, BYTECOUNT		; checks for count
						MOV AL, BUFFER[EDI]		; get char from buff
						CMP AL, NULL			; check for null
						JE END_Main_Loop		; nothing in buff
						CMP AL, ' ' ; test for space character
						JE NEXTCHAR	; skip the space
						CMP AL, TAB	; test for tab
						JNE ENDSKIP ; process the cmd
		NEXTCHAR:
						INC EDI	; inc the buff
						JMP SKIP ; run loop again
		ENDSKIP:

		SWITCH:
						CMP AL, -1			; test if we don't have anything to process
						JE ENDCASE			; run loop again

		CASE1:			CMP AL, '-'			; test if the character is a -
						JNE CASE1A			; process next case
						INC EDI				; move to next location
						CMP EDI, byteCount	; test if within input string
						JGE CASESUB			; nothing after - it is subtraction
						MOV AL, buffer[EDI]	; get the next character
						CMP AL, NULL		; test for null
						JE CASE2			; nothing after - it is subtraction
						CMP AL, '0'			; test for a digit
						JL CASESUB			; not a digit; it is subtraction
						CMP AL, '9'
						JG CASESUB			; not a digit it is subtraction
						JMP CASENEG

		CASE1A:			CMP AL, '0'			
                        JL CASE2
						CMP AL, '9'
						JLE CASEPOS
						JMP CASE2
                        

		CASEPOS:		CALL POSNUMBER		; process pos num
						JMP ENDCASE			; jump to end

		CASENEG:		CALL NEGNUMBER		; process neg num
						MOV ESI, EAX		; move eax to ptr
						JMP ENDCASE			; cycle

		CASESUB:		CALL STACK_CHECK	; check for stacks amt
						CMP EDX, 1			; test
						JE ENDCASE			; cycle if not enough in stack for operation
						CALL SUBTRACTION	; process subtraction
						JMP ENDCASE			; cycle main loop

		CASE2:			CMP AL, '+'			; test if the character is a +
						JNE CASE3			; process next case
						CALL STACK_CHECK	; ck for stack amt
						CMP EDX, 1			; test
						JE ENDCASE			; cycle
						CALL ADDITION		; process addition
						JMP ENDCASE			; cycle main loop

		CASE3:			CMP AL, '*'			; tests for multiply
						JNE CASE4			; process next case
						CALL STACK_CHECK	; ck for amt
						CMP EDX, 1			; test
						JE ENDCASE			; cycle
						CALL MULTIPLICATION	; process mult
						JMP ENDCASE			; cycle main

		CASE4:			CMP AL, '/'			; test for slash
						JNE CASE5			; process next case
						CALL STACK_CHECK	; check for stack amt
						CMP EDX, 1
						JE ENDCASE
						CALL DIVIDE			; divide if good
						JMP ENDCASE

		CASE5:			CMP AL, 'Q'			; ck for Q
						JNE CASE6
						CALL QUIT			; process if good
						JMP ENDCASE	

		CASE6:			CMP AL, 'X'			; ck for X
						JNE CASE7
						CALL STACK_CHECK	; check stack size
						CMP EDX, 1
						JE ENDCASE
						CALL EXCHANGE		; process if good
						JMP ENDCASE

		CASE7:			CMP AL, 'N'			; ck for N
						JNE CASE8
						CALL NEGATE			; process if good
						JMP ENDCASE

		CASE8:			CMP AL, 'U'			; ck for U
						JNE CASE9
						CALL STACK_CHECK	; check stack size
						CMP EDX, 1
						JE ENDCASE
						CALL ROLLUP			; process if good
						JMP ENDCASE

		CASE9:			CMP AL, 'D'			; check for D
						JNE CASE10
						CALL STACK_CHECK	; check stack size
						CMP EDX, 1
						JE ENDCASE
						CALL ROLLDOWN		; process if good
						JMP ENDCASE

		CASE10:			CMP AL, 'V'			; check for V
						JNE CASE11
						CALL VIEW			; process if good
						JMP ENDCASE

		CASE11:			CMP AL, 'C'			; check for C
						JNE DEFAULT
						CALL CLEAR			; process if equal
						JMP ENDCASE

		CASE12:			

		DEFAULT:		MOV EDX, offset INVALIDCMD	; if no match
						CALL WRITESTRING			; write error prompt
						CALL CRLF
		ENDCASE:		
						MOV EDX, 0					; reset for flag
						JMP MAIN_LOOP
		End_Main_Loop:

		
	main ENDP

	POPIT PROC
		PUSH ESI				; save the esi register
		CMP STACKINDEX, 0		; test if stack is empty
		JL NOPOP
		MOV ESI, STACKINDEX		; get the stack index
		MOV EAX, NUMSTACK[ESI]	; extract the value from the stack
		SUB STACKINDEX, 4		; update the stack index
		CLC						; clear carry to show success
		JMP ENDPOP
	NOPOP:
		STC						; set carry to show the stack is empty
		MOV EDX, OFFSET EMPTY	; display exit message
		CALL WRITESTRING
		CALL CRLF
	ENDPOP:
		POP ESI					; restore the register
		RET						; return
	POPIT ENDP

	PUSHIT PROC
		PUSH ESI				; save the ESI register
		CMP STACKINDEX, (STACKSIZE-1)*4	;test if the stack is full
		JGE NOPUSH
		ADD STACKINDEX, 4		; update the stack index
		MOV ESI, STACKINDEX		; get the stackindex
		MOV NUMSTACK[ESI], EAX	; put the value onto the stack
		CLC						; indicate success
		JMP ENDPUSH
	NOPUSH:
		STC						; indicate the stack is full
		MOV EDX, OFFSET FULL	; display exit message
		CALL WRITESTRING
		CALL CRLF

	ENDPUSH:
		POP ESI					; restore the register
		RET
	PUSHIT ENDP

	POSNUMBER PROC
			MOV EAX, 0			; initalize reg eax
			SUB AL, 30H			; conv from hex num
BEG_LOOP:
			INC EDI
			MOV EBX, 0				; load register
			MOV BL, BUFFER[EDI]		; check to see if next char is digit
			CMP BL, '9'
			JG END_LOOP
			CMP BL, '0'
			JL END_LOOP
			SUB BL, 30H				; conv from hex
			IMUL EAX, 10H			; multiply number by 10
			ADD AL, BL				; adds next digit
			JMP BEG_LOOP
END_LOOP:
			CALL PARSEINTEGER32		; parse to int from string
			CALL PUSHIT				; add to stk
			CALL PRINT				; print top
			RET
	POSNUMBER ENDP

	NEGNUMBER PROC
			MOV EAX, 0				; initialize eax
			SUB AL, 30H				; conv to dec
NEG_LOOP:		
			INC EDI					; check next for digit
			MOV EBX, 0				; load register
			MOV BL, BUFFER[EDI]		; check to see if next char is digit
			CMP BL, '9'
			JG NEG_LOOP_END
			CMP BL, '0'
			JL NEG_LOOP_END
			SUB BL, 30H
			IMUL EAX, 10H			; multiply number by 10
			ADD AL, BL				; adds next digit
			IMUL EAX, -1
			JMP NEG_LOOP
NEG_LOOP_END:	
			CALL PARSEINTEGER32		; 
			CALL PUSHIT
			CALL PRINT
			RET
	NEGNUMBER ENDP

	SUBTRACTION PROC
		CALL POPIT
		MOV EBX, EAX
		CALL POPIT
		SUB EAX, EBX
		CALL PUSHIT
		CALL PRINT
		RET
	SUBTRACTION ENDP

	ADDITION PROC
		CALL POPIT
		MOV EBX, EAX
		CALL POPIT
		ADD EAX, EBX
		CALL PUSHIT
		CALL PRINT
		RET
	ADDITION ENDP

	MULTIPLICATION PROC
		CALL POPIT
		MOV EBX, EAX
		CALL POPIT
		IMUL EAX, EBX
		CALL PUSHIT
		CALL PRINT
		RET
	MULTIPLICATION ENDP

	DIVIDE PROC
		CALL POPIT
		MOV EBX, EAX
		CALL POPIT
		CDQ
		IDIV EAX
		CALL PUSHIT
		CALL PRINT
		RET
	DIVIDE ENDP

	QUIT PROC
		MOV EDX, OFFSET TEXIT		; display exit message
		CALL WRITESTRING
		CALL CRLF
		INVOKE EXITPROCESS, 0
		RET
	QUIT ENDP

	EXCHANGE PROC
			CALL POPIT
			MOV EBX, EAX
			CALL POPIT
			XCHG EBX, EAX
			CALL PUSHIT
			MOV EAX, EBX
			CALL PUSHIT
			CALL PRINT
			RET
	EXCHANGE ENDP

	NEGATE PROC
		CALL POPIT
		IMUL EAX, -1
		CALL PUSHIT
		CALL PRINT
		RET
	NEGATE ENDP
					
	ROLLUP PROC
					MOV ECX, 0
					MOV ESI, STACKINDEX		; get the stackindex
					CMP ESI, 0				;test if stack index is not less than 0 or empty
					CALL POPIT
					SUB ESI, 4
					MOV EBX, EAX
ROLLUP_LOOP:
					CMP ESI, 0
					JL	ROLL_P
					CALL POPIT
					SUB ESI, 4
					PUSH EAX
					ADD ECX, 4 
					JMP ROLLUP_LOOP
ROLL_P:
					MOV EAX, EBX
					CALL PUSHIT
ROLL_P_END:

ADD_LOOP:			
					CMP ECX, 0
					JLE	ADD_LOOP_END
					POP EAX
					CALL PUSHIT
					SUB ECX, 4
					JMP	ADD_LOOP			; cycle through loop
ADD_LOOP_END:

ROLLUP_LOOP_END:
					CALL PRINT
					RET
	ROLLUP ENDP
		
	ROLLDOWN PROC
					MOV ESI, STACKINDEX
					MOV EBX, 0
					

		RLOOP:
					MOV EAX, 0
					CALL POPIT
					PUSH EAX
					ADD EBX, 4
					SUB ESI, 4
					CMP ESI, 0
					JGE RLOOP
		END_RLOOP:
					POP ECX

		DLOOP:		
					MOV EAX, 0
					POP EAX
					CALL PUSHIT
					SUB EBX, 4
					CMP EBX, 4
					JG DLOOP

					MOV EAX, ECX
					CALL PUSHIT
		END_DLOOP:
	END_ROLLD:
					CALL PRINT
					RET
	ROLLDOWN ENDP
					

	VIEW PROC
			MOV ESI, STACKINDEX			; copy index into a register for processing
		VIEWLOOP:
			CMP ESI, 0					; check size of stack index
			JL	ENDVIEWLOOP				; return if nothing in stack
			MOV EAX, NUMSTACK[ESI]		; get value from location of stack/index register
			CALL WRITEINT
			CALL CRLF
			SUB ESI, 4					; subtract 4 to move through stack
			JMP VIEWLOOP				; cycles through loop
		ENDVIEWLOOP:
			RET
	VIEW ENDP
		
	CLEAR PROC
			MOV STACKINDEX, -4
			CLC
			RET
	CLEAR ENDP

	PRINT PROC
		MOV EDX, OFFSET TOPMSG					; MOVE MESSAGE INTO REGISTER
		CALL WRITESTRING						; WRITE STRING TO DISPLAY
		CALL POPIT
		CALL WRITEINT							; WRITE EAX TO SCREEN
		CALL PUSHIT
		CALL CRLF
		RET
	PRINT ENDP

	STACK_CHECK PROC
		MOV ESI, STACKINDEX						; move index to register
        CMP ESI, 4								; operand test
		JGE	END_LABEL
		MOV EDX, OFFSET INSUFFOP				; move error message to edx
        CALL WRITESTRING						; write error message to screen
        CALL CRLF								; new line
		MOV EDX, 1
END_LABEL:	
		RET
	STACK_CHECK ENDP

	END main
