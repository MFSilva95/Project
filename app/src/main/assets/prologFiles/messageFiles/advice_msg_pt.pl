%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                           Advice Messages                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%In this file Messages are inserted or defined
% messages must have the format msg(ID, Message with extra options separated by "-")

msg('Msg1', 'O seu valor de Glicemia encontra-se alto. Um novo teste vai ser agendado para verificar se o valor estabiliza./adviceTimer-testGlucose-600-s/').
msg('Msg2', '').
msg('Msg3', '').

msg('Tsk1', 'Já não testa o seu valor de Glicemia à demasiado tempo. O ultimo valor registado tem o valor expirado por favor faça um novo teste/10').
msg('Tsk2', '').
msg('Tsk3', '').


%%Default Message in case of no advice triggered
msg('MsgF', _).