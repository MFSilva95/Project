%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                           Advice Messages                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%types are: NORMAL, SUGGESTION, QUESTION, ALERT};

msg('hasHypoGlycemia', ['You have a low Glycemia value.', 'You should eat 15 grams of carbohydrates and re-test your glycaemia values. \n\n A new glycaemia registry has already been scheduled.',['ALERT','alarmMessage','Glycemia',6,s],9]).

msg('hasLowGlucose_exercise_insulin',  ['Low Glucose after exercising!','You shouldnt adminstrate such a large dose of insulin when exercising. ',['NORMAL'],5]).


msg('hasBetterWeight',  ['Congratulations!', 'Congratulations! you ve been loosing weight.',['NORMAL'],6 ]).
msg('hasBetterWeight_wealthy',  ['Congratulations!', 'Congratulations! You have a weight within healthy standards',['NORMAL'],6 ]).











%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Task Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Essencial Task messages
%%------------------------------------------------------------------------------------------------------------

msg('Tsk_glycemiaReg', 'J� n�o testa a sua Glicemia � demasiado tempo. O �ltimo valor registado tem o valor expirado. Por favor fa�a um novo teste./10').
msg('Tsk_hbA1cReg', 'J� n�o testa o seu valor de HbA1c � demasiado tempo. O �ltimo valor registado tem o valor expirado. Por favor fa�a um novo teste./10').
msg('Tsk_arterialPReg', 'J� n�o testa o seu valor de Press�o Arterial � demasiado tempo. O �ltimo valor registado tem o valor expirado. Por favor fa�a um novo teste./10').
msg('Tsk_weightReg', 'J� n�o mede o seu Peso � demasiado tempo. O �ltimo valor registado tem o valor expirado. Por favor fa�a uma nova pesagem./10').
msg('Tsk_cholesterolReg', 'J� n�o testa o seu Colesterol � demasiado tempo. O �ltimo valor registado tem o valor expirado. Por favor fa�a um novo teste./10').

%%------------------------------------------------------------------------------------------------------------
%% Other Task messages
%%------------------------------------------------------------------------------------------------------------

msg('Tsk_doExerciseTwoTimesADay', 'Deve fazer Exerc�cio F�sico e regista-lo, pelo menos duas vezes ao dia.').

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
msg(tskPers4, meal, 'Registar nova Refei��o').

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Null Message                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%Default Message in case of no advice triggered
msg('NULL', '').