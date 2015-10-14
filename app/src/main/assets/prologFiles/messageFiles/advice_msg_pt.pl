%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                           Advice Messages                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%In this file Messages are inserted or defined
% messages must have the format msg(ID, Message with extra options separated by "-")

msg('hasHighGlucose', 'O seu valor Glicemico encontra-se alto. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-Glycemia-600-s/').
msg('hasLowGlucose', 'O seu valor Glicemico encontra-se baixo. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-Glycemia-600-s/').

msg('hasHighHbA1c', 'O seu valor de HbA1c encontra-se alto. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-HbA1c-600-s/').
msg('hasLowHbA1c', 'O seu valor de HbA1c encontra-se baixo. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-HbA1c-600-s/').

msg('hasHighCholesterol', 'O seu valor de Colesterol encontra-se alto. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-Cholesterol-600-s/').
msg('hasLowCholesterol', 'O seu valor de Colesterol encontra-se baixo. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-Cholesterol-600-s/').

msg('hasHighInsulin', 'O Valor de insulina inserido é muito alto. Um teste irá ser agendado para assegurar que o valor inserido não criou uma crise de hipoglicemia./adviceTimer-Glycemia-600-s/').

msg('hasHighWeight', 'O seu peso encontra-se muito alto. Por favor tenha cuidado com o seu peso./simpleAdvice/').
msg('hasLowWeight', 'O seu peso encontra-se muito baixo. Por favor tenha cuidado com o seu peso./simpleAdvice/').

msg('hasHighArterialPressure', 'O seu valor de pressão arterial encontra-se alto. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-BloodPressure-600-s/').
msg('hasLowArterialPressure', 'O seu valor de pressão arterial encontra-se baixo. Um novo teste irá ser agendado para verificar se este valor estabiliza./adviceTimer-BloodPressure-600-s/').

msg('mealExercisedRecently', 'Recentemente fez exercício físico. Tenha em atenção que o valor de insulina sugerido pode não corresponder à dose real, pois o seu cálculo é feito sem ter em conta o exercício físico./simpleAdvice/').
msg('mealUserWithHighWeight', 'Modere as suas refeições e tenha cuidado com o seu peso./simpleAdvice/').

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Task Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Essencial Task messages
%%------------------------------------------------------------------------------------------------------------

msg('Tsk_glycemiaReg', 'Já não testa a sua Glicemia à demasiado tempo. O último valor registado tem o valor expirado. Por favor faça um novo teste./10').
msg('Tsk_hbA1cReg', 'Já não testa o seu valor de HbA1c à demasiado tempo. O último valor registado tem o valor expirado. Por favor faça um novo teste./10').
msg('Tsk_arterialPReg', 'Já não testa o seu valor de Pressão Arterial à demasiado tempo. O último valor registado tem o valor expirado. Por favor faça um novo teste./10').
msg('Tsk_weightReg', 'Já não mede o seu Peso à demasiado tempo. O último valor registado tem o valor expirado. Por favor faça uma nova pesagem./10').
msg('Tsk_cholesterolReg', 'Já não testa o seu Colesterol à demasiado tempo. O último valor registado tem o valor expirado. Por favor faça um novo teste./10').

%%------------------------------------------------------------------------------------------------------------
%% Other Task messages
%%------------------------------------------------------------------------------------------------------------

msg('Tsk_doExerciseTwoTimesADay', 'Deve fazer Exercício Físico e regista-lo, pelo menos duas vezes ao dia.').

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                            Personalized Tasks                                              %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Defines the text of the possible tasks and their parameter
%%------------------------------------------------------------------------------------------------------------

msg(tskPers1, glicemia, 'Testar Glicemia').
msg(tskPers2, exercise, 'Registar novo Exercicio').
msg(tskPers3, insulin, 'Registar nova dose de Insulina').
msg(tskPers4, meal, 'Registar nova Refeição').

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Null Message                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%Default Message in case of no advice triggered
msg('NULL', '').