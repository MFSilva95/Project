%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                           Advice Messages                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%In this file Messages are inserted or defined
% messages must have the format msg(ID, Message with extra options separated by "-")

msg('Msg1', 'Your Glycemia value is high. A new test is going to be agended to check if your value estabilizes./adviceTimer-testGlucose-600-s/').
msg('Msg2', '').
msg('Msg3', '').

msg('Tsk1', 'Your Glycemia value time has expired. Please take a new test as quickly as possible/10').
msg('Tsk2', '').
msg('Tsk3', '').


%%Default Message in case of no advice triggered
msg('MsgF', _).