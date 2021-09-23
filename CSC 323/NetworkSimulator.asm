COMMENT!
		ASSEMBLY PROGRAM 4- NETWORK SIMULATOR
		CSC-323: ASSEMBLY LANGUAGE PROGRAMMING
		GROUP 2
		ANDREW SPATE		&		NICHOLAS SPUDICH
		SPA3195@CALU.EDU	&		SPU8504@CALU.EDU


;--------------------------------------------------------------------------------------------------------------------------------------------
										; IMPORTANT: NOTES :IMPORTANT
;--------------------------------------------------------------------------------------------------------------------------------------------
; KNOWN PROBLEMS WITH THE PROGRAM:
		; DOESN'T PRINT NUMBERS TO OUTPUT FILE/PRINTING DOESN'T ALIGN CORRECTLY
				; TRIED:
						; TRIED MOVING STATEMENT INTO FILE AT INDEX
						; TRIED STORING IT INTO BUFFER
						; TRIED CONVERTING TO STRING
						; TRIED INSERTING NEW LINES TO FORMAT CORRECTLY AND MANUALLY PUT 13, 10 INTO MSG FOR CRLF.
		; SENDPACKET SEEMS TO STORE INTO RECEIVER BUFFER, BUT PACKETS SEEM TO GET LOST, CAUSING INFINITE LOOP
				; TRIED:
						; TRIED REWRITING THE SENDPACKET USING A PARENT NODE/CHILD NODE LOOP
						; TRIED MESSING AROUND WITH THE RECEIVE BUFFER TO SEE IF IT WASN'T PICKING UP THE PACKETS???
		; RECEIVE LOOP/BUFFER ISSUE
				; TRIED:
						; IF THE SENDPACKET IS MESSED UP, THE RECEIVE LOOP MUST ALSO HAS SOME ISSUE AS IT LOSES THE PACKETS
		; ECHO/NO ECHO
				; TRIED:
						; SEEING AS THE SENDPACKET OR RECEIVE BUFFER WERE NOT WORKING CORRECTLY A ECHO COULD NOT BE SET UP PROPERLY TO WORK
						; IF THEY WERE THERE WOULD BE A COMPARISON STATEMENT TO CHECK WHETHER THE ECHOF WAS TRUE/FALSE TO DICTATE WHETHER 
						; A NODE WOULD SEND BACK TO A ORIGIN/SOURCE.
;--------------------------------------------------------------------------------------------------------------------------------------------
!
INCLUDE Irvine32.inc;	INCLUDES LIBRARY FOR INPUT-OUTPUT AND STRING HANDLING

.data
;--------------------------------------------------------------------------------------------------------------------------------------------
													; CONSTANTS
;--------------------------------------------------------------------------------------------------------------------------------------------
TAB							EQU			9																		; TAB CHARACTER
MESSAGESINQUEUE				EQU			30																		; XMT SIZE
PACKETSIZE					EQU			8																		; SIZE OF PACKETS
QUEUESIZE					EQU			(MESSAGESINQUEUE+1)*PACKETSIZE											; SIZE OF QUEUES
BASESIZEOFSTRUCTURE			EQU			14																		; BASE SIZE OF STRUCTURE
CONNECTIONSIZE				EQU			12																		; ADDITIONAL SIZE FOR EACH CONNCECTION
NAMEOFFSET					EQU			0																		; OFFSET TO NODE NAME IN STRUCTURE
NUMCONNOFFSET				EQU			1																		; OFFSET TO NUMBER OF CONNECTIONS
FIXEDSIZE					EQU			14																		; SIZE OF FIXED SPACE
NODEOFFSET					EQU			2																		; CHECK IF NODE IS ORIGINATOR
TRUE						EQU			1
FALSE						EQU			0
BUFFERSIZE					EQU			81																		; SIZE OF INPUT BUFFER
FILEBUFFERSIZE				EQU			100																		; FILE BUFFER SIZE
NULL						EQU			0																		; NULL
;===================
; NODE OFFSETS
;===================
CONNECTIONOFFSET			EQU			0																		; CONNECTION OFFSET
QUEUEADDRESS				EQU			2																		; QUEUE ADDRESS OFFSET
XMTOFFSET					EQU			4																		; OFFSET TO TRANSMIT BUFFER PTR
INPTROFFSET					EQU			6																		; IN-PTR OFFSET
RCVOFFSET					EQU			8																		; OFFSET TO RECEIVE BUFFER PTR
OUTPTROFFSET				EQU			10																		; OUT-PTR OFFSET
TRANSMITOFFSET				EQU			18																		; TRANSMIT BUFFER OFFSET
RCVBUFFOFFSET				EQU			22																		; RECEIVER BUFFER OFFSET
;===================
; PACKET OFFSETS
;===================
DESTOFFSET					EQU			0																		; DESTINATION OFFSET
SENDOFFSET					EQU			1																		; SENDER OFFSET
ORIGOFFSET					EQU			2																		; ORIGINATOR OFFSET
TTLOFFSET					EQU			3																		; TIME TO LIVE OFFSET
RCVTIMEOFFSET				EQU			4																		; RECEIVE TIME OFFSET
;--------------------------------------------------------------------------------------------------------------------------------------------
													; VARIABLES
;--------------------------------------------------------------------------------------------------------------------------------------------
DEFAULTHOPS					EQU			6
INITPACKET					BYTE		'D', 'A','A', DEFAULTHOPS												; DESTINATION D, SENDER A, ORIGINATOR A, TTL6
							WORD		0
TEMPPACKET					BYTE		PACKETSIZE DUP(0)														; TEMPPACKET BUFFER																	
NODEPOINTER					DWORD		NODEA																	; CURRENT NODE POINTER
NODENAME					BYTE		' '																		; NODE NAME
NODEFROM					BYTE		' '																		; NODE FROM NAME
MESSAGEPOINTER 				DWORD		TEMPPACKET																; CURRENT NODE POINTER
SAVENODEPOINTER				DWORD		?
SAVENUM						BYTE		0
ECHOF						BYTE		FALSE																	; ECHO/NO ECHO FLAG
TIME						WORD		0																		; SYSTEM TIME
NEWPACKETS					WORD		0																		; NUMBER OF NEW PACKETS GENERATED
GENERATEDPACKETS			WORD		0																		; NUMBER OF GENERATED PACKETS
TOTALPACKETS				WORD 		0																		; NUMBER OF TOTAL GENERATED PACKETS
ACTIVEPACKETS				WORD		1																		; NUMBER OF ACTIVE PACKETS
RECIEVEDPACKETS				WORD		0																		; NUMBER OF PACKETS RECIEVED BY DESTINATION
TOTALHOPS					WORD		0																		; TOTAL HOPS OF PACKETS TO REACH DESTINATION
TOTALTIME					WORD		0																		; TOTAL TIME OF PACKETS TO REACH THE DESTINATION
AVGHOPS						REAL8		0.0																		; AVERAGE HOPS OF PACKETS TO REACH DESTINATION
AVGTIME						REAL8		0.0																		; AVERAGE TIME PER HOP
PERCENTHIT					REAL8		0.08
MAXHOPS						BYTE 		6																		; MAX NUMBER OF HOPS
ECHOBUFF					BYTE		10 DUP(0)																; BUFFER FOR USER INPUT ON ECHOMODE
ECHOSIZE					DWORD		?																		; VARIABLE FOR SIZE OF ECHO RESPONSE


;===================
;	DATA BUFFERS
;===================
INFILEHANDLE				DWORD		?																		; INPUT FILE HANDLE
OUTFILEHANDLE				DWORD		?																		; OUTPUT FILE HANDLE
FILENAME					BYTE		BUFFERSIZE DUP(0)														; FILE NAME BUFFER
FILEBUFFER					BYTE		FILEBUFFERSIZE DUP(0)													; FILE BUFFER
BYTE									0																		; SPACE FOR THE NULL AFTER THE READ
BYTESREAD					DWORD		0																		; NUMBER OF BYTES READ


;===================
;	EOF LABELS
;===================
AVG_HOPS					BYTE		"AVG HOPS: ", 0	
AVG_TIME					BYTE		"AVG TIME: ", 0
PERCENT_DEST				BYTE		"PERCENT_DEST: ", 0

;===================
;	TEST LABELS
;===================
ATOB	BYTE	"A TO B: ", 0
ATOE	BYTE	"A TO E: ", 0
BTOA	BYTE	"B TO A: ", 0
BTOC	BYTE	"B TO C: ", 0
BTOF	BYTE	"B TO F: ", 0
CTOB	BYTE	"C TO B: ", 0
CTOD	BYTE	"C TO D: ", 0
CTOE	BYTE	"C TO E: ", 0
DTOC	BYTE	"D TO C: ", 0
DTOE	BYTE	"D TO E: ", 0
DTOF	BYTE	"D TO F: ", 0
ETOA	BYTE	"E TO A: ", 0
ETOC	BYTE	"E TO C: ", 0
ETOF	BYTE	"E TO F: ", 0
FTOB	BYTE	"F TO B: ", 0
FTOD	BYTE	"F TO D: ", 0
FTOE	BYTE	"F TO E: ", 0
;--------------------------------------------------------------------------------------------------------------------------------------------
													; MESSAGES
;--------------------------------------------------------------------------------------------------------------------------------------------
CURRENT_NODE				BYTE		"NODE: ",13,0																; processing node message with space for node name at end
CONNECTION_NODE				BYTE		"CONNECTION: ",13,0														; CONNECTION NODE MESSAGE WITH SPACE FOR NODE NAME AT THE END
NODEPOSITIONOFFSET			EQU			SIZEOF CURRENT_NODE-2													; OFFSET INTO NODE MESSAGE FOR NODE NAME
CONNECTIONPOSITIONOFFSET	EQU			SIZEOF CONNECTION_NODE-2												; OFFSET INTO CONNECTION MESSAGE FOR NODE NAME
ECHOMESS					BYTE		"ECHO ON",13, 0															; ECHO ON MSG					
NOECHOMESS					BYTE		"ECHO OFF",13, 0															; ECHO OFF MSG
PROMPTINPUTFILE				BYTE		"ENTER INPUT FILE NAME: ", 0											; PROMPT FOR INPUT FILE
PROMPTOUTPUTFILE			BYTE		"ENTER OUTPUT FILE NAME: ", 0											; PROMPT FOR OUTPUT FILE
FILEERRORMESSAGE			BYTE		"ERROR OPENING FILE.", 0												; FILE ERROR MESSAGE
FILEREADMESSAGE				BYTE		"ERROR READING FILE.", 0												; FILE ERROR MESSAGE
FILEWRITEMESSAGE			BYTE		"ERROR WRITING FILE.", 0												; FILE ERROR MESSAGE
TEXIT						BYTE		"PROGRAM FINISHED RUNNING...", 0										; EXIT PRGM MESSAGE
ECHOPROMPT					BYTE		"ENTER 1 FOR ECHO MODE, OR 2 FOR NO ECHO MODE", 0						; PROMPT FOR ECHOMODE
TRYAGAIN					BYTE		"Let's try this again...", 0											; TRY AGAIN PROMPT
;===================
; MSG LABELS
;===================
SOURCENODE					BYTE		"SOURCE NODE:  ", 13,0														; SOURCE NODE LABEL
TTLVALUE					BYTE		"TTL: ", 13,0																; TTL VALUE LABEL	
TIMEIS						BYTE		"TIME IS ", 0															; TIME IS LABEL
NEWMSGS						BYTE		" NEW MESSAGES.",13, 0														; NEW MESSAGE LABEL
MESSAGERECEIVED				BYTE		" THE MESSAGE WAS RECEIVED FROM  ",13, 0									; MESSAGE RECEIVED MSG
MESSAGEGENERATED			BYTE		TAB, TAB, TAB,"A MESSAGE IS GENERATED FOR  ",13, 0							; MESSAGE GENERATED MSG
MESSAGESENT					BYTE		TAB, TAB, TAB, TAB,"THE MESSAGE WAS SENT.",13, 0							; MESSAGE SENT MSG
MESSAGENOTSENT				BYTE		TAB, TAB, TAB, TAB,"THE MESSAGE WAS NOT SENT.",13, 0						; MESSAGE NOT SENT MSG
PROCESSINGOUT				BYTE		TAB, "PROCESSING OUTGOING QUEUE OF  ",13, 0								; PROCESSING OUTGOING MSG
ATTIME						BYTE		TAB, TAB,"AT TIME ", 0													; AT TIME LABEL
THEREARE1					BYTE		TAB, TAB,"THERE ARE ", 0												; THERE ARE1 LABEL
THEREARE2					BYTE		"THERE ARE ", 0															; THERE ARE2 LABEL
MESSAGESACTIVEAND			BYTE		"MESSAGE(S) ACTIVE, ", 0												; MESSAGES ACTIVE AND LABEL
MESSAGESHAVEBEEN			BYTE		"MESSAGE(S) HAVE BEEN GENERATED IN THIS TIME, AND A TOTAL OF ", 0		; MESSAGES HAVE BEEN GENERATED LABEL
TOTALMESSAGESHAVEBEEN		BYTE		"MESSAGE(S) EXISTED IN THE NETWORK.", 13,0				
;===================
; RECEIVER LABELS
;===================
RECEIVERBUFF				BYTE		"PROCESSING THE RECEIVER BUFFERS OF  ", 13,0								; PROCESS RECEIVER BUFFER LABEL
GOTMSG						BYTE		TAB,"A MESSAGE WAS RECEIVED FROM  ", 13,0									; MSG RECEIVED LABEL
MSGDIED						BYTE		TAB,TAB,"THE MESSAGE DIED.", 13,0											; MSG DIED LABEL
RCVDESTINATION				BYTE		TAB,TAB,"THE MESSAGE REACHED IT'S DESTINATION FROM  ",13, 0				; MSG RECEIVED DESTINATION LABEL
;--------------------------------------------------------------------------------------------------------------------------------------------
													; BUFFERS
;--------------------------------------------------------------------------------------------------------------------------------------------
;===================
; BUFFER FOR NODE A
;===================
AXMTB	LABEL				BYTE																				; A TRANSMIT BUFFER TO b LABEL TO A BYTE SPACE
BRCVA	BYTE				PACKETSIZE DUP(0)																	; B RECIEVE BUFFER FROM A PACKETSIZE BYTES
AXMTE	LABEL				BYTE																				; A TRANSMIT BUFFER TO E LABEL TO A BYTE SPACE
ERCVA	BYTE				PACKETSIZE DUP(0)																	; E RECIEVE BUFFER FROM A PACKETSIZE BYTES
;===================
;BUFFER FOR NODE B
;===================
BXMTA	LABEL				BYTE																				; B TRANSMIT BUFFER TO 	A LABEL TO A BYTE SPACE
ARCVB	BYTE				PACKETSIZE DUP(0)																	; A RECIEVE BUFFER FROM B PACKETSIZE BYTES
BXMTC	LABEL				BYTE																				; B TRANSMIT BUFFER TO C LABEL TO A BYTE SPACE
CRCVB	BYTE				PACKETSIZE DUP(0)																	; C RECIEVE BUFFER FROM B PACKETSIZE BYTES
BXMTF	LABEL				BYTE																				; B TRANSMIT BUFFER TO F LABEL PACKETSIZE BYTES
FRCVB	BYTE				PACKETSIZE DUP(0)																	; F RECIEVE BUFFER FROM B PACKETSIZE BYTES
;===================
;BUFFER FOR NODE C
;===================
CXMTB	LABEL				BYTE																				; C TRANSMIT BUFFER TO 	B LABEL TO A BYTE SPACE
BRCVC	BYTE				PACKETSIZE DUP(0)																	; B RECIEVE BUFFER FROM C PACKETSIZE BYTES
CXMTD	LABEL				BYTE																				; C TRANSMIT BUFFER TO D LABEL TO A BYTE SPACE
DRCVC	BYTE				PACKETSIZE DUP(0)																	; D RECIEVE BUFFER FROM C PACKETSIZE BYTES
CXMTE	LABEL				BYTE																				; C TRANSMIT BUFFER TO E LABEL PACKETSIZE BYTES
ERCVC	BYTE				PACKETSIZE DUP(0)																	; E RECIEVE BUFFER FROM C PACKETSIZE BYTES
;===================
;BUFFER FOR NODE D
;===================
DXMTC	LABEL				BYTE																				; D TRANSMIT BUFFER TO C LABEL TO A BYTE SPACE
CRCVD	BYTE				PACKETSIZE DUP(0)																	; C RECIEVE BUFFER FROM D PACKETSIZE BYTES
DXMTF	LABEL				BYTE																				; D TRANSMIT BUFFER TO F LABEL PACKETSIZE BYTES
FRCVD	BYTE				PACKETSIZE DUP(0)																	; F RECIEVE BUFFER FROM D PACKETSIZE BYTES
;===================
;BUFFER FOR NODE E
;===================
EXMTA	LABEL				BYTE																				; E TRANSMIT BUFFER TO 	A LABEL TO A BYTE SPACE
ARCVE	BYTE				PACKETSIZE DUP(0)																	; A RECIEVE BUFFER FROM E PACKETSIZE BYTES
EXMTC	LABEL				BYTE																				; E TRANSMIT BUFFER TO C LABEL TO A BYTE SPACE
CRCVE	BYTE				PACKETSIZE DUP(0)																	; C RECIEVE BUFFER FROM E PACKETSIZE BYTES
EXMTF	LABEL				BYTE																				; E TRANSMIT BUFFER TO F LABEL PACKETSIZE BYTES
FRCVE	BYTE				PACKETSIZE DUP(0)																	; F RECIEVE BUFFER FROM E PACKETSIZE BYTES
;===================
; BUFFER FOR NODE F
;===================
FXMTB	LABEL				BYTE																				; F TRANSMIT BUFFER TO B LABEL TO C BYTE SPACE
BRCVF	BYTE				PACKETSIZE DUP(0)																	; B RECIEVE BUFFER FROM F PACKETSIZE BYTES
FXMTD	LABEL				BYTE																				; F TRANSMIT BUFFER TO D LABEL TO A BYTE SPACE
DRCVF	BYTE				PACKETSIZE DUP(0)																	; D RECIEVE BUFFER FROM F PACKETSIZE BYTES
FXMTE	LABEL				BYTE																				; F TRANSMIT BUFFER TO E LABEL TO A BYTE SPACE
ERCVF	BYTE				PACKETSIZE DUP(0)																	; E RECIEVE BUFFER FROM F PACKETSIZE BYTES
;--------------------------------------------------------------------------------------------------------------------------------------------
													; QUEUES
;--------------------------------------------------------------------------------------------------------------------------------------------
QUEUEA						BYTE QUEUESIZE DUP(0)																; QUEUE A
QUEUEB						BYTE QUEUESIZE DUP(0)																; QUEUE B
QUEUEC						BYTE QUEUESIZE DUP(0)																; QUEUE C
QUEUED						BYTE QUEUESIZE DUP(0)																; QUEUE D
QUEUEE						BYTE QUEUESIZE DUP(0)																; QUEUE E
QUEUEF						BYTE QUEUESIZE DUP(0)																; QUEUE F

NETWORK	LABEL				BYTE																				; ENTRY TO START OF NETWORK
;--------------------------------------------------------------------------------------------------------------------------------------------
													; NODES
;--------------------------------------------------------------------------------------------------------------------------------------------
;===================
; NODE A
;===================
NODEA						BYTE	'A'																			; NAME OF NODE
							BYTE	2																			; NUMBER OF CONNECTIONS
							DWORD	QUEUEA																		; ADDRESS OF QUEUEA
							DWORD	QUEUEA																		; IN POINTER INITIALIZED TO QUEUE A
							DWORD	QUEUEA																		; OUT POINTER INITIALIZED TO QUEUE A
;--------------------------------------------------------------------------------------------------------------------------------------------						
							DWORD	NODEB																		; POINTER TO NODE B
							DWORD	AXMTB																		; POINTER TO TRANSMIT BUFFER FROM A TO B
							DWORD	ARCVB																		; POINTER TO RECIEVE BUFFER FROM B TO A
;--------------------------------------------------------------------------------------------------------------------------------------------							
							DWORD	NODEE																		; POINTER TO NODE E
							DWORD	AXMTE																		; POINTER TO TRANSMIT BUFFER FROM A TO E
							DWORD	ERCVA																		; POINTER TO RECIVE BUFFER FROM E TO A
;===================
; NODE B
;===================
NODEB						BYTE	'B'																			; NAME OF NODE
							BYTE	3																			; NUMBER OF CONNECTIONS
							DWORD	QUEUEB																		; ADDRESS OF QUEUEB
							DWORD	QUEUEB																		; IN POINTER INITIALIZED TO QUEUE B
							DWORD	QUEUEB																		; OUTPOINTER INITIALIZED TO QUEUE B
;--------------------------------------------------------------------------------------------------------------------------------------------
							DWORD	NODEA																		; POINTER TO NODE A
							DWORD	BXMTA																		; POINTER TO TRANSMIT BUFFER FROM B TO A
							DWORD	BRCVA																		; POINTER TO RECIVEVE BUFFER FROM A TO B
;--------------------------------------------------------------------------------------------------------------------------------------------
							DWORD	NODEC																		; POINTER TO NODE C
							DWORD	BXMTC																		; POINTER TRANSMIT BUFFER FROM B TO C
							DWORD	BRCVC																		; POINTER TO RECIEVE BUFFER FROM C TO B
;--------------------------------------------------------------------------------------------------------------------------------------------							
							DWORD	NODEF																		; POINTER TO NODE F
							DWORD	BXMTF																		; POINTER TO TRANSMIT BUFFER FROM B TO F
							DWORD	BRCVF																		; POINTER TO RECIEVE BUFFER FROM F TO B
;===================
; NODE C
;===================
NODEC						BYTE	'C'																			; NAME OF NODE
							BYTE	3																			; NUMBER OF CONNECTIONS
							DWORD	QUEUEC																		; ADDRESS OF QUEUEC
							DWORD	QUEUEC																		; IN POINTER INITIALIZED TO QUEUE C
							DWORD	QUEUEC																		; OUTPOINTER INITIALIZED TO QUEUE C
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEB																		; POINTER TO NODE B
							DWORD	CXMTB																		; POINTER TO TRANSMIT BUFFER FROM C TO B
							DWORD	CRCVB																		; POINTER TO RECIEVE BUFFER FROM B TO C
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODED																		; POINTER TO NODE D
							DWORD	CXMTD																		; POINTER TRANSMIT BUFFER FROM C TO D
							DWORD	CRCVD																		; POINTER TO RECIEVE BUFFER FROM D TO C
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEE																		; POINTER TO NODE E
							DWORD	CXMTE																		; POINTER TO TRANSMIT BUFFER FROM C TO E
							DWORD	CRCVE																		; POINTER TO RECIEVE BUFFER FROM E TO C
;===================
; NODE D
;===================
NODED						BYTE	'D'																			; NAME OF NODE
							BYTE	2																			; NUMBER OF CONNECTIONS
							DWORD	QUEUED																		; ADDRESS OF QUEUED
							DWORD	QUEUED																		; IN POINTER INITIALIZED TO QUEUED
							DWORD	QUEUED																		; OUTPOINTER INITIALIZED TO QUEUE D
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEC																		; POINTER TO NODE C
							DWORD	DXMTC																		; POINTER TRANSMIT BUFFER FROM D TO C
							DWORD	DRCVC																		; POINTER TO RECIEVE BUFFER FROM C TO D
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEF																		; POINTER TO NODE F
							DWORD	DXMTF																		; POINTER TO TRANSMIT BUFFER FROM D TO F
							DWORD	DRCVF																		; POINTER TO RECIEVE BUFFER FROM F TO D
;===================
; NODE E
;===================
NODEE						BYTE	'E'																			; NAME OF NODE
							BYTE	3																			; NUMBER OF CONNECTIONS
							DWORD	QUEUEE																		; ADDRESS OF QUEUEB
							DWORD	QUEUEE																		; IN POINTER INITIALIZED TO QUEUEB
							DWORD	QUEUEE																		; OUTPOINTER INITIALIZED TO QUEUE E
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEA																		; POINTER TO NODE A
							DWORD	EXMTA																		; POINTER TO TRANSMIT BUFFER FROM E TO A
							DWORD	ERCVA																		; POINTER TO RECIVEVE BUFFER FROM A TO E
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEC																		; POINTER TO NODE C
							DWORD	EXMTC																		; POINTER TRANSMIT BUFFER FROM E TO C
							DWORD	ERCVC																		; POINTER TO RECIEVE BUFFER FROM C TO E
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEF																		; POINTER TO NODE F
							DWORD	EXMTF																		; POINTER TO TRANSMIT BUFFER FROM E TO F
							DWORD	ERCVF																		; POINTER TO RECIEVE BUFFER FROM F TO E
;===================
; NODE F
;===================
NODEF						BYTE	'F'																			; NAME OF NODE
							BYTE	3																			; NUMBER OF CONNECTIONS
							DWORD	QUEUEF																		; ADDRESS OF QUEUEA
							DWORD	QUEUEF																		; IN POINTER INITIALIZED TO QUEUEF
							DWORD	QUEUEF																		; OUT POINTER INITIALIZED TO QUEUEF
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEB																		; POINTER TO NODEC
							DWORD	FXMTB																		; POINTER TO TRANSMIT BUFFER FROM F TO B
							DWORD	FRCVB																		; POINTER TO RECIEVE BUFFER FROM B TO F
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODED																		; POINTER TO NODE D
							DWORD	FXMTD																		; POINTER TO TRANSMIT BUFFER FROM F TO D
							DWORD	FRCVD																		; POINTER TO RECIEVE BUFFER FROM D TO F
;--------------------------------------------------------------------------------------------------------------------------------------------								
							DWORD	NODEE																		; POINTER TO NODE E
							DWORD	FXMTE																		; POINTER TO TRANSMIT BUFFER FROM F TO E
							DWORD	FRCVE																		; POINTER TO RECIEVE BUFFER FROM E TO F

ENDOFNODES					DWORD	ENDOFNODES																	; END OF NODE STRUCTURES
;--------------------------------------------------------------------------------------------------------------------------------------------
													.CODE
;--------------------------------------------------------------------------------------------------------------------------------------------
												;===================
												;	REGISTERS KEY
												;===================
									; EDI POINTER TO BEGINING OF CURRENT NODE STRUCTURE
									; ESI POINTER TO CONNECTED NODE STRUCTURE
									; EDX USED FOR MESSAGE ADDRESS BY WRITESTRING
									; ECX USED FOR MESSAGE LENGTH BY WRITESTRING
									; EBX USED FOR CONNECTION COUNTER
									; EAX TEMPORARY REGISTER FOR DATA AND CALCULATIONS
;--------------------------------------------------------------------------------------------------------------------------------------------
													MAIN PROC
;--------------------------------------------------------------------------------------------------------------------------------------------
				FINIT																							; INITIALIZE FPU FOR FLOATING POINT
				CALL INITIALIZE																					; INITIALIZE START OF PROGRAM(FULLQUEABORT)
	WHILE1:
				CALL XMT_LOOP																					; PROCESS TRANSMIT LOOP
				CALL STEPTIME																					; STEP TIME
				CALL RCV_LOOP																					; PROCESS RECEIVE LOOP
				CMP ACTIVEPACKETS, 0																			; TEST IF THERE ARE ACTIVE PACKETS
				JNE WHILE1																						; LOOP UNTIL 0
	ENDWHILE1:
				CALL QUIT																						; PROCESS EXIT
	MAIN ENDP																									; END OF MAIN PROC

	;----------------------------
	; AFTER THE MAIN LOOP
	;----------------------------
	; WHEN THE PROCESS IS COMPLETE, ZERO ACTIVE MESSAGES, PRINT SIMULATION INFORMATION:
			; TOTAL TIME FOR SIMULATION
			; TOTAL MESSAGE GENERATED IN THE SIMULATION
			; TOTAL PACKETS THAT REACHED THE DESTINATION
			; AVERAGE HOPS TO REACH THE DESTINATION
			; PERCENTAGE OF MESSAGES THAT REACHED THE DESTINATION
;--------------------------------------------------------------------------------------------------------------------------------------------
													; PROCEDURES
;--------------------------------------------------------------------------------------------------------------------------------------------
;===================
INITIALIZE PROC	
;===================
				CALL ECHOMODE																					; PROMPT USER TO SET ECHO ON OR OFF
				CALL OPENOUTPUTFILE																				; PROMPT FOR USER TO CREATE OR OPEN FILE FOR WRITING
				MOV TIME, 0																						; SYSTEM TIME
				MOV TOTALPACKETS, 1																				; INIT NUMBER OF TOTAL GENERATED PACKETS
				MOV ACTIVEPACKETS, 1																			; NUMBER OF ACTIVE PACKETS
				MOV RECIEVEDPACKETS, 0																			; NUMBER OF PACKETS RECIEVED BY THE DESTINATION
				MOV TOTALHOPS, 0																				; TOTAL HOPS OF PACKETS TO REACH DESTINATION
				MOV TOTALTIME, 0																				; TOTAL TIME OF PACKETS TO REACH DESTINATION
				;----------------------------																	; DEFINE THE INITIAL PACKET
				MOV EDI, OFFSET INITPACKET																		; GET ADDRESS OF INITPACKET
				MOV AL, DESTOFFSET[EDI]																			; GET DESTINATION
				MOV NODENAME, AL																				; SET DESTINATION
				MOV AL, SENDOFFSET[EDI]																			; GET SOURCE
				MOV NODEFROM, AL																				; SET SOURCE
				MOV AL, TTLOFFSET[EDI]																			; GET TTL
				MOV MAXHOPS, AL																					; SET TTL
				;----------------------------
				MOV NODEPOINTER, OFFSET NODEA																	; PUT NODE A, BEGINNING OF STRUCTURE
				MOV EDI, NODEPOINTER																			; GET CURRENT NODE POINTER
				MOV BL, NODEFROM																				; GET SOURCE NODE
				;----------------------------
	FINDSOURCE:																									; LOCATE SOURCE NODE
				CMP BYTE PTR NODEOFFSET[EDI], BL																; CHECK IF THIS IS THE SOURCE NODE
				JE FINISHFIND																					; FOUND THE SOURCE NODE
				MOV ECX, 0																						; CLEAR ECX
				MOV CL, NUMCONNOFFSET[EDI]																		; GET NUMBER OF CONNECTIONS
				ADD EDI, BASESIZEOFSTRUCTURE																	; MOVE TO CONNECTION SPACE OF STRUCTURE
				MOV EAX, CONNECTIONSIZE																			; GET SIZE OF EACH CONNECTION
				MUL ECX																							; DETERMINE SIZE OF ALL BL CONNECTIONS
				ADD EDI, EAX																					; OFFSET ESI OVER ALL CONNECTIONS TO NEXT NODE
				MOV NODEPOINTER, EDI																			; UPDATE NODEPOINTER WITH NEW NODE
				CMP EDI, ENDOFNODES																				; TEST FOR END OF NODES
				JL FINDSOURCE																					; CHECK NEXT NODE
				MOV NODEPOINTER, OFFSET NODEA																	; REQUESTED NODE NOT FOUND, USE NODE A
				MOV EDI, NODEPOINTER
				;----------------------------	
	FINISHFIND:																									; ADD A PACKET TO THE TRANSMIT QUEUE
				MOV MESSAGEPOINTER, OFFSET INITPACKET															; PUT INIT PACKET ADDRESS IN MESSAGE ADDRESS
				CALL PUTIT																						; COPY INIT MESSAGE INTO NODE A TRANSMIT QUEUE
				JC FULLQUEUEABORT																				; FULL TRANSMIT QUEUE, ABORT PROGRAM
				;----------------------------																	; POSITION TO INSERTION POINT FOR NODE NAME
				MOV EDX, OFFSET SOURCENODE																		; GET SOURCE NODE: MESSAGE ADDRESS
				MOV EAX, SIZEOF SOURCENODE																		; GET SIZE OF SOURCE NODE MESSAGE
				ADD EDX, EAX																					; ADD TOGETHER TO POINT TO THE END
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION IN STRING
				MOV AL, NODEFROM																				; GET NODE NAME
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE
				;----------------------------
				MOV EDX, OFFSET SOURCENODE																		; GET SOURCE NODE: MESSAGE ADDRESS
				MOV ECX, SIZEOF SOURCENODE																		; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				;----------------------------																	; PRINT TTL
				MOV EDX, OFFSET TTLVALUE																		; GET TTL: MESSAGE ADDRESS
				MOV ECX, SIZEOF TTLVALUE																		; GET SIZE OF MESSAGE
				MOV EAX, 0																						; ZERO EAX FOR BYTE INFORMATION
				MOV AL, MAXHOPS																					; GET THE MAXIMUM NUMBER OF HOPS
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT MESSAGE
				;----------------------------																	; PRINTING ECHO/NO-ECHO INFORMATION
				MOV EDX, OFFSET ECHOMESS																		; GET ECHO MESSAGE ADDRESS
				MOV ECX, SIZEOF ECHOMESS																		; GET SIZE OF OUTPUT BUFFER
				CMP ECHOF, TRUE																					; COMPARE ECHOF TO TRUE
				JE PRINTECHO																					; JUMP TO PRINT ECHO IF TRUE
				MOV EDX, OFFSET NOECHOMESS																		; GET NO-ECHO MESSAGE ADDRESS
				MOV ECX, SIZEOF NOECHOMESS																		; GET SIZE OF OUTPUT BUFFER
	PRINTECHO:
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				;----------------------------																	; PROCESS TRANSMIT
				CALL PRINTCRLF																					; PRINT NEW LINE
				;----------------------------							
				MOV NODEPOINTER, OFFSET NODEA																	; INIT NODE PTR TO NODE A STRUCTURE
				MOV GENERATEDPACKETS, 0																			; INIT GENERATED PACKETS FOR THIS TIME
				RET
FULLQUEUEABORT:
				CALL QUIT
	INITIALIZE ENDP
;===================
	XMT_LOOP PROC
;===================
				MOV NODEPOINTER, OFFSET NODEA
				MOV EDX, OFFSET TIMEIS																			; PRINT TIME IS MESSAGE
				MOV ECX, SIZEOF TIMEIS																			; GET SIZE OF MESSAGE
				MOV EAX, 0																						; ZERO EAX FOR BYTE INFORMATION
				MOV AX, TIME																					; GET NUMBER TO PRINT
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT THE MESSAGE AND THE NUMBER
	XMTLOOP:																									; START OF XMT LOOP
				MOV ESI, NODEPOINTER																			; GET NODE STRUCTURE ADDRESS
				MOV EDI, NODEPOINTER
				MOV EDX, OFFSET PROCESSINGOUT																	; GET PROCESSING...MESSAGE ADDRESS
				MOV EAX, SIZEOF PROCESSINGOUT																	; GET SIZE OF PROCESSING MESSAGE
				ADD EDX, EAX																					; ADD TOGETHER
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION
				;----------------------------																	; MOVE NODE NAME INTO MESSAGE
				MOV AL, NAMEOFFSET[ESI]																			; GET NODE NAME
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE
				MOV NODENAME, AL																				; KEEP NODE NAME
				MOV EDX, OFFSET PROCESSINGOUT																	; GET PROCESSING...MESSAGE ADDRESS
				MOV ECX, SIZEOF PROCESSINGOUT																	; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				;----------------------------																	; GET THE PACKET FROM THE TRANSMIT QUEUE
				MOV MESSAGEPOINTER, OFFSET TEMPPACKET															; GET TEMPORARY PACKET LOCATION
				MOV EDX, MESSAGEPOINTER
				CALL GETIT																						; CHECK IF THERE IS A MESSAGE TO TRANSMIT
				JC MOVETONEXTXMT																				; NO MESSAGE IN QUEUE, MOVE TO THE NEXT NODE
				;----------------------------																	; ONLY IF THERE IS A MESSAGE TO SEND, PROCESS THE CONNECTIONS
				;----------------------------																	; GET NUMBER OF CONNECTIONS TO PROCESS FOR THE NODE
				MOV EBX, 0																						; CLEAR EBX
				MOV BL, NUMCONNOFFSET[ESI]																		; GET NUMBER OF CONNECTIONS
				MOV EBP, OFFSET TEMPPACKET																		; GET TEMP PACKET ADDRESS, THIS HAS THE PACKET
				;----------------------------																	; PRINT AT TIME...
				MOV EDX, OFFSET ATTIME																			; GET AT TIME...MESSAGE ADDRESS
				MOV ECX, SIZEOF ATTIME																			; GET SIZE OF MESSAGE
				MOV EAX, 0																						; ZERO EAX FOR BYTE INFORMATION
				MOV AX, RCVTIMEOFFSET[EBP]																		; GET TIME FROM PACKET
				CLC																								; DO NO INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT MESSAGE
				;----------------------------																	; PRINT PACKET RECEIVED FROM INFORMATION
				MOV EDX, OFFSET MESSAGERECEIVED																	; GET MESSAGE RECEIVED FROM...
				MOV EAX, SIZEOF MESSAGERECEIVED																	; GET SIZE OF MESSAGE RECEIVED FROM
				ADD EDX, EAX																					; ADD TOGETHER
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION
				;----------------------------																	; GET NODE FROM MESSAGE IN XMT QUEUE PACKET, TEMPPACKET
				MOV AL, SENDOFFSET[EBP]																			; GET SENDING NODE
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE
				MOV NODEFROM, AL																				; KEEP SENDING NODE NAME
				MOV AL, NODENAME										
				MOV SENDOFFSET[EBP], AL																			; UPDATE NODE NAME IN MESSAGE
				MOV EDX, OFFSET MESSAGERECEIVED																	; GET MESSAGE RECEIVED FROM...MESSAGE ADDRESS
				MOV ECX, SIZEOF MESSAGERECEIVED																	; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE
				;----------------------------																	; INITIALIZE PACKET COUNTERS
				MOV NEWPACKETS, -1																				; RESET NEW PACKET COUNTER
				DEC GENERATEDPACKETS																			; ADJUST PACKETS SO RECEIVED PACKET, ONE FROM TRANSMIT QUEUE NOT DOUBLED
				DEC TOTALPACKETS																				; ...
				DEC ACTIVEPACKETS																				; ...
				;----------------------------																	; PROCESS EACH CONNECTION
				ADD ESI, BASESIZEOFSTRUCTURE																	; MOVE TO CONNECTION SPACE OF STRUCTURE
	XMTNODELOOP:
				MOV EDX, OFFSET MESSAGEGENERATED																; GET MESSAGE GENERATED FOR...MESSAGE
				MOV EAX, SIZEOF MESSAGEGENERATED																; GET SIZE OF MESSAGE GENERATED FOR MESSAGE
				ADD EDX, EAX																					; ADD TOGETHER
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION
				;----------------------------																	; GET CONNECTION NODE NAME
				MOV EDI, CONNECTIONOFFSET[ESI]																	; GET ADDRESS OF CONNECTED NODE STRUCTURE
				MOV AL, NAMEOFFSET[EDI]																			; GET NODE NAME
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE
				MOV EDX, OFFSET MESSAGEGENERATED																; GET MESSAGE GENERATED FOR...MESSAGE
				MOV ECX, SIZEOF MESSAGEGENERATED																; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				;----------------------------																	; ECHO OR NO ECHO
				CMP ECHOF, TRUE																					; TEST IF ECHO
				JE SENDIT
				CMP NODEFROM, AL																				; CHECK IF CONNECTION IS THE SENDER
				JE DONTSEND
	SENDIT:
				INC ACTIVEPACKETS																				; COUNT THE ACTIVE PACKETS
				INC NEWPACKETS																					; COUNT THE NEW MESSAGE
				INC GENERATEDPACKETS																			; COUNT THE GENERATED PACKETS
				INC TOTALPACKETS																				; COUNT THE TOTAL GENERATED PACKETS
				;----------------------------																	; COPY TEMPPACKET TO TRANSMIT BUFFER FOR THIS CONNECTION
				CALL SENDPACKET																					; SEND THE MESSAGE
				MOV EDX, OFFSET MESSAGESENT																		; GET OUTPUT BUFFER
				MOV ECX, SIZEOF MESSAGESENT																		; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				JMP MOVETONEXTXMT
				;----------------------------
	DONTSEND:
				MOV EDX, OFFSET MESSAGENOTSENT																	; GET OUTPUT BUFFER
				MOV ECX, SIZEOF MESSAGENOTSENT																	; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				;----------------------------
				MOV EDX, OFFSET THEREARE1
				MOV ECX, SIZEOF THEREARE1
				MOV EAX, 0
				MOV AX, ACTIVEPACKETS
				CLC
				CALL PRINTMESSAGENUMBER
				MOV EDX, OFFSET NEWMSGS
				MOV ECX, SIZEOF NEWMSGS
				STC
				CALL PRINTMESSAGE
				;----------------------------
MOVETONEXTXMT:																									; MOVE TO NEXT CONNECTION IN CURRENT STRUCTURE
				ADD ESI, CONNECTIONSIZE																			; MOVE TO NEXT CONNECTION
				DEC EBX																							; COUNT THE PROCESSED NODE CONNECTION
				JG XMTNODELOOP																					; PROCESS NEXT CONNECTION OF CURRENT NODE
				;----------------------------
MOVETONEXTNODE:
				MOV EAX, 0																						; CLEAR EAX
				MOV ESI, NODEPOINTER
				MOV AL, NUMCONNOFFSET[ESI]																		; GET NUMBER OF CONNECTIONS
				MOV CL, CONNECTIONSIZE																			; GET SIZE OF EACH CONNECTION							
				MUL CL																							; DETERMINE SIZE OF ALL BL CONNECTIONS
				ADD EAX, BASESIZEOFSTRUCTURE																	; MOVE TO CONNECTION SPACE OF STRUCTURE
				ADD NODEPOINTER, EAX																			; UPDATE NODE POINTER
				MOV EDX, NODEPOINTER																			; MOVE IT INTO A REG TO CHECK
				CMP EDX, ENDOFNODES																				; TEST FOR ENDOFNODES
				JL XMTLOOP																						; REPEAT LOOP IF NOT
XMTLOOP_END:
				RET
	XMT_LOOP ENDP
;===================
	STEPTIME PROC
;===================
				CALL PRINTCRLF																					; PRINT NEW LINE
				MOV EDX, OFFSET THEREARE2																		; PRINT THERE ARE...MESSAGE
				MOV ECX, SIZEOF THEREARE2																		; GET SIZE OF MESSAGE
				MOV EAX, 0																						; CLEAR NUMBER TO PRINT
				MOV AX, ACTIVEPACKETS																			; GET ACTIVE PACKETS TO PRINT
				CLC																								; DO NOT INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT THE MESSAGE AND THE NUMBER
				MOV EDX, OFFSET MESSAGESACTIVEAND																; PRINT ACTIVE MESSAGES
				MOV ECX, SIZEOF MESSAGESACTIVEAND																; GET SIZE OF MESSAGE
				MOV EAX, 0																						; CLEAR THE NUMBER TO PRINT
				MOV AX, GENERATEDPACKETS																		; DO NOT INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT THE MESSAGE AND THE NUMBER
				;----------------------------
				MOV EDX, OFFSET MESSAGESHAVEBEEN																; PRINT HAVE BEEN GENERATED
				MOV ECX, SIZEOF MESSAGESHAVEBEEN																; GET SIZE OF MESSAGE
				MOV EAX, 0																						; CLEAR THE NUMBER TO PRINT
				MOV AX, TOTALPACKETS																			; PRINT NUMBER OF GENERATED PACKETS
				CLC																								; DO NOT INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT THE MESSAGE AND THE NUMBER
				MOV EDX, OFFSET TOTALMESSAGESHAVEBEEN															; PRINT TOTAL HAVE BEEN GENERATED
				MOV ECX, SIZEOF TOTALMESSAGESHAVEBEEN															; GET SIZE OF MESSAGE
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT MESSAGE
				INC TIME																						; INCREMENT TIME
				;----------------------------
				RET
	STEPTIME ENDP
;===================
	RCV_LOOP PROC
;===================
				CALL CRLF
				MOV EDX, OFFSET TIMEIS																			; PRINT TIME IS MESSAGE
				MOV ECX, SIZEOF TIMEIS																			; GET SIZE OF MESSAGE
				MOV EAX, 0																						; ZERO EAX FOR BYTE INFORMATION
				MOV AX, TIME																					; GET NUMBER TO PRINT
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT THE MESSAGE AND THE NUMBER
				MOV ESI, 0
				MOV NODEPOINTER, OFFSET NODEA																	; RESET NODE POINTER
				MOV NEWPACKETS, 0																				; RESET THE NEW PACKETS COUNTER
				;----------------------------																	; PRE-LOAD EDI FOR LOOP
				MOV ESI, NODEPOINTER
				ADD ESI, BASESIZEOFSTRUCTURE
				MOV EDI, CONNECTIONOFFSET[ESI]																	; GET ADDRESS OF CONNECTED NODE STRUCTURE
				MOV EDI, RCVBUFFOFFSET[EDI]																		; SET EDI TO XMT/RCV BUFFER SPACE
				;----------------------------
	RCVLOOP:
				MOV ESI, NODEPOINTER
				MOV EBX, 0																						; CLEAR EBX
				MOV BL, NUMCONNOFFSET[ESI]																		; MOVE NUM OF CONNECTIONS INTO BL
				MOV EDX, OFFSET RECEIVERBUFF																	; GET MESSAGE RECEIVED FROM...
				MOV EAX, SIZEOF RECEIVERBUFF																	; GET SIZE OF MESSAGE RECEIVED FROM
				ADD EDX, EAX																					; ADD TOGETHER
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION
				;----------------------------																	; GET NODE FROM MESSAGE IN XMT QUEUE PACKET, TEMPPACKET
				MOV AL, NAMEOFFSET[ESI]																			; GET SENDING NODE
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE										
				MOV NODENAME, AL
				MOV EDX, OFFSET RECEIVERBUFF																	; GET MESSAGE RECEIVED FROM...MESSAGE ADDRESS
				MOV ECX, SIZEOF RECEIVERBUFF																	; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE
				;----------------------------
				ADD ESI, BASESIZEOFSTRUCTURE																	; MOVE TO CONNECTION SPACE OF STRUCTURE
				JMP TESTFORMSG
				;----------------------------
MOVETONEXTRCV:																									; MOVE TO NEXT CONNECTION IN CURRENT STRUCTURE
				ADD ESI, CONNECTIONSIZE																			; MOVE TO NEXT CONNECTION
				DEC EBX																							; COUNT THE PROCESSED NODE CONNECTION
				CMP EBX, 0																						; TEST FOR END OF CONNECTIONS
				JE RCVNEXTNODE																					; MOVE TO NEXT NODE
				MOV EDI, CONNECTIONOFFSET[ESI]																	; GET ADDRESS OF CONNECTED NODE STRUCTURE
				MOV EDI, RCVBUFFOFFSET[EDI]																		; SET EDI TO XMT/RCV BUFFER SPACE
TESTFORMSG:				
				CMP BYTE PTR DESTOFFSET[EDI], 0																	; CHECK IF MESSAGE RECEIVED
				JE MOVETONEXTRCV																				; PROCESS NEXT CONNECTION OF CURRENT NODE
				;----------------------------
MSGRECEIVED:
				MOV EDX, OFFSET GOTMSG																			; GET MESSAGE RECEIVED FROM...
				MOV EAX, SIZEOF GOTMSG																			; GET SIZE OF MESSAGE RECEIVED FROM
				ADD EDX, EAX																					; ADD TOGETHER
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION
				;----------------------------																	
				MOV AL, SENDOFFSET[EDI]																			; GET DEST NODE USING XMT/RCV BUFF ADDRESS IN EDI
				MOV NODEFROM, AL
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE									
				MOV EDX, OFFSET GOTMSG																			; GET MESSAGE RECEIVED FROM...MESSAGE ADDRESS
				MOV ECX, SIZEOF GOTMSG																			; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE
				;----------------------------																	; CHECK IF THE MESSAGE IS INTEDED FOR YOU
				CMP	AL, DESTOFFSET[EDI]																			; COMPARE THE DESTINATION WITH CURRENT NODE 
				JE GOAL																							; PROCESSING IT'S RECEIVE BUFFERS
				SUB BYTE PTR TTLOFFSET[EDI], 1																	; DECREMENT THE TTL COUNTER IN THE PACKET
				;----------------------------
	ALIVE:
				CMP BYTE PTR TTLOFFSET[EDI], 0																	; CHECK IF THE PACKET DIED
				JE DIED																							; PACKET DIED BEFORE GOAL
				MOV AX, TIME
				MOV RCVTIMEOFFSET[EDI], AL																		; UPDATE THE PACKET'S RECEIVE TIME WITH THE CURRENT SYSTEM TIME
				;----------------------------
				MOV EBP, OFFSET TEMPPACKET
				MOV AL, DESTOFFSET[EDI]																			; GET RECEIVER BUFFER DESTINATION
				MOV DESTOFFSET[EBP], AL																			; SET TEMPPACKET SENDER/ORIG
				MOV AL, ORIGOFFSET[EDI]
				MOV ORIGOFFSET[EBP], AL
				MOV AL, NODEFROM															
				MOV SENDOFFSET[EBP], AL																			; SET TEMPPACKET DESTINATION
				MOV AL, TTLOFFSET[EDI]																			; GET RECEIVER BUFFER TTL
				MOV TTLOFFSET[EBP], AL																			; SET TEMPPACKET TTL
				MOV AL, RCVTIMEOFFSET[EDI]																		; GET RECEIVER BUFFER RECEIVE TIME TO LIVE
				MOV RCVTIMEOFFSET[EBP], AL																		; SET TEMPPACKET RECEIVE TIME TO LIVE
				;----------------------------
				MOV AL, NODEFROM																				; MOVE THE LETTER OF NODE TO BE SEND INTO AL
				CMP AL, 'A'
				JE CASEA
				CMP AL, 'B'
				JE CASEB
				CMP AL, 'C'
				JE CASEC
				CMP AL, 'D'
				JE CASED
				CMP AL, 'E'
				JE CASEE
				CMP AL, 'F'
				JE CASEF
	CASEA:
				MOV EDI, OFFSET NODEA
				JMP ENDCASE
	CASEB:
				MOV EDI, OFFSET NODEB
				JMP ENDCASE
	CASEC:
				MOV EDI, OFFSET NODEC
				JMP ENDCASE
	CASED:
				MOV EDI, OFFSET NODED
				JMP ENDCASE
	CASEE:
				MOV EDI, OFFSET NODEE
				JMP ENDCASE
	CASEF:
				MOV EDI, OFFSET NODEF
	ENDCASE:
				MOV MESSAGEPOINTER, OFFSET TEMPPACKET															; MOVE THE PACKET INTO MESSAGEPOINTER
				CALL PUTIT																						; PUT IT INTO THE TRANSMIT QUEUE
				MOV EDI, CONNECTIONOFFSET[ESI]																	; RESTORE CONNECTION OFFSET TO ESI
				MOV EDI, RCVBUFFOFFSET[EDI]
				MOV BYTE PTR DESTOFFSET[EDI], 0																	; CLEAR THE RECEIVE BUFFER
				JMP MOVETONEXTRCV																				; MOVE TO NEXT CONNECTION
				;----------------------------
	GOAL:																										; WHEN A MESSAGE IS RECEIVED THAT IS FOR THIS NODE
				INC RECIEVEDPACKETS																				; INCREMENT THE RECEIVED PACKETS COUNTER
				MOV AL, MAXHOPS																					; MOVE MAX HOPS IN AL
				SUB AL, TTLOFFSET[EDI]																			; CALCULATE HOW MANY HOPS IT TOOK TO GET TO YOU
				ADD TOTALHOPS, AX																				; ADD AL TO TOTALHOPS
				;----------------------------																	; PREPARE A MESSAGE RECEIVED MESSAGE
				MOV EDX, OFFSET RCVDESTINATION																	; GET MESSAGE RECEIVED FROM...
				MOV EAX, SIZEOF RCVDESTINATION																	; GET SIZE OF MESSAGE RECEIVED FROM
				ADD EDX, EAX																					; ADD TOGETHER
				SUB EDX, 2																						; ADJUST TO NODE NAME POSITION
				;----------------------------																	
				MOV AL, SENDOFFSET[EDI]																			; GET SENDING NODE
				MOV [EDX], AL																					; PUT NODE NAME INTO MESSAGE								
				MOV SENDOFFSET[EDI], AL																			; UPDATE NODE NAME IN MESSAGE
				MOV EDX, OFFSET RCVDESTINATION																	; GET MESSAGE RECEIVED FROM...MESSAGE ADDRESS
				MOV ECX, SIZEOF RCVDESTINATION																	; GET SIZE OF OUTPUT BUFFER
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE
				DEC BYTE PTR ACTIVEPACKETS																		; DECREMENT THE ACTIVE PACKETS COUNTER
				MOV BYTE PTR DESTOFFSET[EDI], 0																	; CLEAR THE RECEIVE BUFFER
				JMP MOVETONEXTRCV																				; PROCEEED TO THE NEXT CONNECTION IN THIS NODE
				;----------------------------																							
	DIED:																										; PREPARE AND PRINT A MESSAGE DIED MESSAGE
				MOV EDX, OFFSET MSGDIED																			; GET MESSAGE RECEIVED FROM...
				MOV EAX, SIZEOF MSGDIED																			; GET SIZE OF MESSAGE RECEIVED FROM
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE
				DEC BYTE PTR ACTIVEPACKETS																		; DECREMENT THE ACTIVE PACKETS COUNTER
				MOV BYTE PTR [EDI], 0																			; CLEAR THE RECEIVE BUFFER
				JMP MOVETONEXTRCV																				; PROCEED TO THE NEXT CONNECTION IN THIS NODE
				;----------------------------
RCVNEXTNODE:
				MOV EAX, 0																						; CLEAR EAX
				MOV ESI, NODEPOINTER
				MOV AL, NUMCONNOFFSET[ESI]																		; GET NUMBER OF CONNECTIONS
				MOV CL, CONNECTIONSIZE																			; GET SIZE OF EACH CONNECTION							
				MUL CL																							; DETERMINE SIZE OF ALL BL CONNECTIONS
				ADD EAX, BASESIZEOFSTRUCTURE																	; MOVE TO CONNECTION SPACE OF STRUCTURE
				ADD NODEPOINTER, EAX																			; UPDATE NODE POINTER
				MOV EDX, NODEPOINTER																			; MOVE IT INTO A REG TO CHECK
				CMP EDX, ENDOFNODES																				; TEST FOR ENDOFNODES
				JL RCVLOOP																						; REPEAT LOOP IF NOT
				;----------------------------
				CALL PRINTCRLF																					; PRINT NEW LINE
				MOV EDX, OFFSET THEREARE2																		; PRINT THERE ARE...MESSAGE
				MOV ECX, SIZEOF THEREARE2																		; GET SIZE OF MESSAGE
				MOV EAX, 0																						; CLEAR NUMBER TO PRINT
				MOV AX, ACTIVEPACKETS																			; GET ACTIVE PACKETS TO PRINT
				CLC																								; DO NOT INCLUDE CRLF
				CALL PRINTMESSAGENUMBER																			; PRINT THE MESSAGE AND THE NUMBER
				MOV EDX, OFFSET MESSAGESACTIVEAND																; PRINT ACTIVE MESSAGES
				MOV ECX, SIZEOF MESSAGESACTIVEAND																; GET SIZE OF MESSAGE
				MOV EAX, 0																						; CLEAR THE NUMBER TO PRINT
				STC																								; INCLUDE CRLF
				CALL PRINTMESSAGE																				; PRINT THE MESSAGE AND THE NUMBER
				CALL CRLF
RCV_LOOP_END:
				RET
	RCV_LOOP ENDP
;===================
	PRINTCRLF PROC
;===================
				CALL CRLF
				RET
	PRINTCRLF ENDP
;===================
	PRINTMESSAGE PROC
;===================
	; PRINTS EDX TO THE DISPLAY AS WELL AS TO THE OUTPUT FILE
	; USED CARRY TO INCLUDE THE CARRIAGE RETURN AND LINE FEED OR NOT
				JNC NO_CRLF
				CALL WRITESTRING
				CALL FILEWRITER
				CALL CRLF
				JMP PRINT_END
	NO_CRLF:
				CALL WRITESTRING
				CALL FILEWRITER
	PRINT_END:
				RET
	PRINTMESSAGE ENDP
;===================
	PRINTMESSAGENUMBER PROC
;===================
	; PRINTS EDX TO THE DISPLAY AS WELL AS TO THE OUTPUT FILE FOLLOWED BY THE NUMBER IN EAX
	; USED CARRY TO INCLUDE THE CARRIAGE RETURN AND LINE FEED OR NOT
				JNC NO_CRLF
				CALL WRITESTRING
				CALL FILEWRITER
				CALL WRITEINT
				CALL FILEWRITERNUM
				
				CALL CRLF
				JMP PRINT_END
	NO_CRLF:
				CALL WRITESTRING
				CALL FILEWRITER
				CALL WRITEINT
				CALL FILEWRITERNUM
				
	PRINT_END:
				RET
	PRINTMESSAGENUMBER ENDP
;===================
	SENDPACKET PROC
;===================
				; COPY TEMPPACKET TO TRANSMIT BUFFER FOR THIS CONNECTION
				MOV EDX, EDI
				PUSH ESI
				PUSH EDI
				PUSH EBX
				PUSH ECX


				MOV BL, AL
				
				MOV EDI, OFFSET TEMPPACKET
				MOV SENDOFFSET[EDI], BL
				CLD	
				MOV AL, NAMEOFFSET[EDX]
				CMP AL, 'A'
				JE CASEA
				CMP AL, 'B'
				JE CASEB
				CMP AL, 'C'
				JE CASEC
				CMP AL, 'D'
				JE CASED
				CMP AL, 'E'
				JE CASEE
				CMP AL, 'F'
				JE CASEF
	CASEA:		
	_ATOB:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET BRCVA
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
	_ATOE:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET ARCVE
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB 
				JMP ENDCASE
	CASEB:			
	_BTOA:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET BRCVA
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB 
	_BTOC:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET BRCVC
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB 
	_BTOF:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET BRCVF
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB 
				JMP ENDCASE
	CASEC:
	_CTOB:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET CRCVB
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB 
	_CTOD:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET CRCVD
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB 
	_CTOE:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET CRCVE
				MOV ECX, SIZEOF CRCVE
				ADD EDI, ECX
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
				JMP ENDCASE
		
	CASED:		
	_DTOC:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET DRCVC
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
	_DTOF:	
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET DRCVF
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
				JMP ENDCASE
	CASEE:
	_ETOA:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET ERCVA
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
	_ETOC:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET ERCVC
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
	_ETOF:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET ERCVF
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
				JMP ENDCASE
	CASEF:
	_FTOB:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET FRCVB
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
	_FTOD:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET FRCVD
				MOV ECX, SIZEOF TEMPPACKET
				REP MOVSB
	_FTOE:
				MOV ESI, OFFSET TEMPPACKET
				MOV EDI, OFFSET FRCVE
				MOV ECX, SIZEOF TEMPPACKET
				JMP ENDCASE
	ENDCASE:									
				;----------------------------	
	SEND_END:	
				POP ECX
				POP EBX
				POP EDI
				POP ESI
				RET
	SENDPACKET ENDP


;===================
	PUTIT PROC
;===================
				PUSH EDX
				MOV EDX, EDI
				PUSH ESI
				PUSH EDI
				PUSH EBX
				PUSH EAX

	FULLQUEUE:																									; CALCULATE IN-PTR AFTER THE PUT
				MOV EAX, INPTROFFSET[EDX]																		; GET IN-PTR
				ADD EAX, PACKETSIZE																				; ADD PACKETSIZE (NEXT LOCATION)
																												; NORMALIZE THE ADDRESS TO BE AN OFFSET INTO THE QUEUE
				SUB EAX, QUEUEADDRESS[EDX]																		; SUBTRACT BASE ADRESS (NORMALIZE OFFSET)
																												; GET OUT-PTR
				MOV EBX, OUTPTROFFSET[EDX]																		; GET OUT-PTR
																												; NORMALIZE OUT-PTR TO BE AN OFFSET INTO THE QUEUE
				SUB EBX, QUEUEADDRESS[EDX]																		; SUBTRACT BASE ADDRESS
																												; COMPARE THE IN AND OUT OFFSETS
				CMP EAX, EBX																					; COMPARE IN TO OUT
				JE QUE_FULL																						; QUEUE FULL
				;----------------------------																	; QUEUE NOT FULL, PROCEED WITH PUT
				CLD
				MOV ESI, MESSAGEPOINTER																			; MESSAGE ADDRESS IN ESI
				MOV EDI, INPTROFFSET[EDX]																		; IN-PTR IN EDI
				MOV ECX, PACKETSIZE																				; GET BYTES TO MOVE
				REP MOVSB																						; MOVE THE BYTES
				;----------------------------																	; UPDATE THE IN-PTR
				MOV EAX, INPTROFFSET[EDX]																		; GET IN-PTR
				ADD EAX, PACKETSIZE																				; UPDATE IN-PTR
																												; CALCULATE THE END OF QUEUE
				MOV EBX, QUEUEADDRESS[EDX]																		; START OF QUEUE
				ADD EBX, QUEUESIZE																				; END OF QUEUE
																												; CHECK IF IN-PTR PAST END OF QUEUE
				CMP EAX, EBX																					; IN-PTR PAST END OF QUEUE?
				JL PUT1																							; NOT PAST END OF QUEUE
				;----------------------------																	; MAKE IT CIRCULAR
				MOV EAX, QUEUEADDRESS[EDX]																		; GET START OF QUEUE
	PUT1:
				MOV INPTROFFSET[EDX], EAX																		; UPDATE IN-PTR
				CLC
				JMP PUTIT_END
				;----------------------------																	; CHECK IF FULLQUEUE
	QUE_FULL:
				STC																								; SET THE CARRY FLAG
				;----------------------------
	PUTIT_END:
				
				POP EAX
				POP EBX
				POP EDI
				POP ESI
				POP EDX
				RET
	PUTIT ENDP
;===================
	GETIT PROC
;===================
				PUSH EDX
				MOV EDX, EDI
				PUSH ESI
				PUSH EDI
				PUSH EBX
				PUSH EAX


				MOV EAX, INPTROFFSET[EDX]
				MOV EBX, OUTPTROFFSET[EDX]																		; GET OUT-PTR
				CMP EAX, EBX																					; OUT-PTR PAST END OF QUEUE?
				JE QUE_EMPTY																					; NOT PAST END OF QUEUE																	
				;----------------------------
	QUE_N_EMPTY:
				CLD
				MOV ESI, OUTPTROFFSET[EDX]																		; OUT-PTR IN ESI
				MOV EDI, MESSAGEPOINTER																			; MSG ADDRESS IN EDI
				MOV ECX, PACKETSIZE																				; GET BYTES TO MOVE
				REP MOVSB																						; MOVE THE BYTES
			
				MOV EAX, OUTPTROFFSET[EDX]
				ADD EAX, PACKETSIZE

				MOV EBX, QUEUEADDRESS[EDX]																		; GET START OF QUEUE
				ADD EBX, QUEUESIZE

				CMP EAX, EBX
				JL QUE_GOOD
	
				MOV EAX, QUEUEADDRESS[EDX]
	QUE_GOOD:
				MOV OUTPTROFFSET[EDX], EAX																		; UPDATE OUT PTR
				CLC
				JMP GETIT_END

	QUE_EMPTY:
				STC
	GETIT_END:
				POP EAX
				POP EBX
				POP EDI
				POP ESI
				POP EDX
				RET
	GETIT ENDP
;===================
	OPENOUTPUTFILE PROC
;===================
				MOV EDX, OFFSET PROMPTOUTPUTFILE																; PUT REFERENCE TO PROMPT IN EDX
				MOV ECX, SIZEOF PROMPTOUTPUTFILE																; PUT SIZE OF BYTES OF PROMPT IN ECX
				CALL WRITESTRING																				; PRINT MESSAGE
				;----------------------------
				MOV EDX, OFFSET FILENAME																		; PUT REFERENCE TO FILENAME IN EDX
				MOV ECX, SIZEOF FILENAME																		; PUT SIZE OF CHARACATERS FOR FILENAME IN ECX
				CALL READSTRING																					; READ THE OUTPUT FILE NAME
				;----------------------------																	; OPEN OUTPUT FILE
				MOV EDX, OFFSET FILENAME																		; PUT FILENAME REFERENCE IN EDX
				CALL CREATEOUTPUTFILE																			; OPEN THE OUTPUT FILE
				MOV OUTFILEHANDLE, EAX																			; KEEP OUTPUT FILE HANDLE
				CMP EAX, INVALID_HANDLE_VALUE																	; TEST IF FILE ERROR
				JNE DONE																						; TERMINATE PROGRAM IF FILE ERROR
		OUTFILEERROR:
				MOV EDX, OFFSET FILEERRORMESSAGE																; PUT REFERENCE TO FILE ERROR MESSAGE IN EDX
				MOV ECX, SIZEOF FILEERRORMESSAGE																; PUT SIZE OF BYTES OF FILE ERROR MESSAGE IN ECX
				CALL WRITESTRING																				; PRINT THE ERROR MESSAGE
				CALL CRLF																						; PRINT CR AND LF
				MOV EAX, INFILEHANDLE																			; PUT THE INPUT FILE HANDLE IN EAX
				CALL CLOSEFILE																					; CLOSE INPUT FILE
				CALL QUIT																						; END THE PROGRAM
		DONE:
				RET
	OPENOUTPUTFILE ENDP
;===================
	FILEWRITER PROC
;===================
				PUSH EAX
				MOV EAX, OUTFILEHANDLE																			; PUT THE OUTPUTFILE HANDLE IN EAX
				CALL WRITETOFILE																				; NO BYTES WERE WRITTEN
				CMP EAX, 0																						;TEST FOR WRITE ERROR
				JNE DONE																						;NO BYTES WERE WRITTEN
	FILEWRITEERROR:
				MOV EDX, OFFSET FILEWRITEMESSAGE																; PUT REFERENCE TO FILE WRITE ERROR MESSAGE IN EDX
				MOV ECX, SIZEOF FILEWRITEMESSAGE																; PUT SIZE OF BYTES OF FILE WRITE ERROR MESSAGE IN ECX
				CALL WRITESTRING																				; PRINT THE ERROR MESSAGE
				CALL CRLF																						; PRINT CR AND LF
				CALL QUIT																						; CLOSE THE FILES
		DONE:		
				POP EAX
				RET

	FILEWRITER ENDP
;===================
	FILEWRITERNUM PROC
;===================
				PUSH EDI
				PUSH EDX
				MOV EDI, OFFSET FILEBUFFER
				STOSD
				MOV EDX, OFFSET FILEBUFFER
				MOV ECX, SIZEOF FILEBUFFER
				MOV EAX, OUTFILEHANDLE																			; PUT THE OUTPUTFILE HANDLE IN EAX
				CALL WRITETOFILE	
				MOV EDX, OFFSET FILEBUFFER
				MOV ECX, SIZEOF FILEBUFFER
				CALL WRITESTRING
				;CALL CRLF
				;CALL CRLF
				;CALL CRLF
				CMP EAX, 0																						;TEST FOR WRITE ERROR
				JNE DONE																						;NO BYTES WERE WRITTEN
	FILEWRITEERROR:
				MOV EDX, OFFSET FILEWRITEMESSAGE																; PUT REFERENCE TO FILE WRITE ERROR MESSAGE IN EDX
				MOV ECX, SIZEOF FILEWRITEMESSAGE																; PUT SIZE OF BYTES OF FILE WRITE ERROR MESSAGE IN ECX
				CALL WRITESTRING																				; PRINT THE ERROR MESSAGE
				CALL CRLF																						; PRINT CR AND LF
				CALL QUIT																						; CLOSE THE FILES
		DONE:		
				
				POP EDX
				POP EDI
				
				RET

	FILEWRITERNUM ENDP


;===================
	ECHOMODE PROC
;===================
ECHOINP:
				MOV EDX, OFFSET ECHOPROMPT																		; MOVE ECHO PROMPT INTO EDX FOR PRINTING
				MOV ECX, SIZEOF ECHOPROMPT																		; MOVE CHARACTER COUNT INTO EDX FOR PRINTING
				CALL WRITESTRING																				; PRINT TO SCREEN
				CALL CRLF
				MOV EDX, OFFSET ECHOBUFF																		; SET UP BUFFER FOR RESPONSE
				MOV ECX, SIZEOF ECHOBUFF													
				CALL READSTRING																					; READ FROM KEYBOARD
				MOV ECHOSIZE, EAX																				; GET SPACES FROM KEYBOARD

				MOV EAX, 0
				MOV AL, ECHOBUFF																				; MOV BUFFER INTO AL FOR PROCESSING
				AND AL, 0FH																						; CONVERT ASCII TO NUMBER
				CMP AL, 1																						; IF TRUE
				JE ECHOON																						; SET ECHOF
				CMP AL, 2																						; IF FALSE
				JE ECHOOFF																						; LEAVE ECHOF
				MOV EDX, OFFSET TRYAGAIN																		; REPROMPT IF NON ANSWER
				MOV ECX, SIZEOF TRYAGAIN
				CALL WRITESTRING																				; PRINT TO SCREEN
				CALL CRLF
				JMP ECHOINP																						; LOOP TO TOP TO TRY AGAIN

	ECHOON:
				MOV ECHOF, TRUE																					; SET FLAG

	ECHOOFF:
				
				RET
	ECHOMODE ENDP
;===================
	PRINTXMTRCVBUFF PROC
;===================
				PUSH EAX
				PUSH EBX
				PUSH ECX
				PUSH EDX
				PUSH EDI
				PUSH ESI
				PUSH EBP

				MOV EDX, OFFSET BTOA
				CALL WRITESTRING
				MOV EDX, OFFSET BRCVA
				CALL WRITESTRING
				CALL CRLF
	
				MOV EDX, OFFSET ETOA
				CALL WRITESTRING
				MOV EDX, OFFSET ERCVA
				CALL WRITESTRING
				CALL CRLF
	
				MOV EDX, OFFSET ATOB
				CALL WRITESTRING
				MOV EDX, OFFSET ARCVB
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET CTOB
				CALL WRITESTRING
				MOV EDX, OFFSET CRCVB
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET FTOB
				CALL WRITESTRING
				MOV EDX, OFFSET FRCVB
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET BTOC
				CALL WRITESTRING
				MOV EDX, OFFSET BRCVC
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET DTOC
				CALL WRITESTRING
				MOV EDX, OFFSET DRCVC
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET ETOC
				CALL WRITESTRING
				MOV EDX, OFFSET ERCVC
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET CTOD
				CALL WRITESTRING
				MOV EDX, OFFSET CRCVD
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET FTOD
				CALL WRITESTRING
				MOV EDX, OFFSET FRCVD
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET ATOE
				CALL WRITESTRING
				MOV EDX, OFFSET ARCVE
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET CTOE
				CALL WRITESTRING
				MOV EDX, OFFSET CRCVE
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET FTOE
				CALL WRITESTRING
				MOV EDX, OFFSET FRCVE
				CALL WRITESTRING
				CALL CRLF
	
				MOV EDX, OFFSET BTOF
				CALL WRITESTRING
				MOV EDX, OFFSET BRCVF
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET DTOF
				CALL WRITESTRING
				MOV EDX, OFFSET DRCVF
				CALL WRITESTRING
				CALL CRLF

				MOV EDX, OFFSET ETOF
				CALL WRITESTRING
				MOV EDX, OFFSET ERCVF
				CALL WRITESTRING
				CALL CRLF

				POP EBP
				POP ESI
				POP EDI
				POP EDX
				POP ECX
				POP EBX
				POP EAX
				RET
	PRINTXMTRCVBUFF ENDP

;===================
	QUIT PROC
;===================
				CALL CRLF
				MOV EDX, OFFSET AVG_HOPS
				CALL WRITESTRING
				FILD TOTALHOPS
				FILD TIME
				FDIV
				CALL WRITEFLOAT
				CALL CRLF

				MOV EDX, OFFSET AVG_TIME
				CALL WRITESTRING
				FILD TOTALTIME
				FILD RECIEVEDPACKETS
				FDIV
				CALL WRITEFLOAT
				CALL CRLF

				MOV EDX, OFFSET PERCENT_DEST
				CALL WRITESTRING
				FILD RECIEVEDPACKETS
				FILD TOTALPACKETS
				FDIV
				CALL WRITEFLOAT
				CALL CRLF

				FST AVGHOPS
				MOV EAX, OUTFILEHANDLE																			; PUT THE OUTPUT FILE HANDLE IN EAX
				CALL CLOSEFILE																					; CLOSE OUTPUT FILE
				MOV EDX, OFFSET TEXIT																			; DISPLAY QUIT PROMPT
				CALL WRITESTRING																				; WRITE IT
				CALL CRLF																						; PRINT CR AND LF
				INVOKE EXITPROCESS, 0																			; EXIT PROGRAM
				RET													
	QUIT ENDP

;--------------------------------------------------------------------------------------------------------------------------------------------
													; END PROGRAM
;--------------------------------------------------------------------------------------------------------------------------------------------
END MAIN														
