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

msg('Tsk_Reg_glucose', 'You dont test your Glucose for far too long. The last registrated value has expired. Please perform a new test./10').
msg('Tsk_Reg_hbA1c', 'You dont test your HbA1c for far too long. The last registrated value has expired. Please perform a new test./10').
msg('Tsk_Reg_arterialP', 'You dont test your Arterial Pressure for far too long. The last registrated value has expired. Please perform a new test./10').
msg('Tsk_Reg_weight', 'You dont weight yourself for far too long. The last registrated value has expired. Please perform a new weighing./10').
msg('Tsk_Reg_cholesterol', 'You dont test your Cholesterol for far too long. The last registrated value has expired. Please perform a new test./10').

%%------------------------------------------------------------------------------------------------------------
%% Other Task messages
%%------------------------------------------------------------------------------------------------------------

msg('Tsk_doTwoTimesADay_exercise', 'You must Exercise at least two times a day and register it.', 'Exercicio fisico is fundamental para uma boa gestao da diabetes', null, 4).
msg('Tsk_doOnceAMonth_exercise', 'You must Exercise at least once a Month and register it.', 'Exercicio fisico is fundamental para uma boa gestao da diabetes', null, 5).

msg(hipoglicemia,[(exercise, '+'), (meal,'-'), (insulin,'+')],'To avoid this situation you should eat before exercising, your insulin intake before exercising could have had an impact on this situation.').
msg(hipoglicemia, [(exercise, '+'),(meal,'-')],'To avoid this situation you should eat before exercising.').
msg(hipoglicemia, [(exercise, '+')], 'your exercise was not well compensated').

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                            Personalized Tasks                                              %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Defines the text of the possible tasks and their parameter
%%------------------------------------------------------------------------------------------------------------

msg(tskPers1, glicemia, 'Test Glucose').
msg(tskPers2, exercise, 'Register new Exercise').
msg(tskPers3, insulin, 'Register new Insulin dose').
msg(tskPers4, meal, 'Register new Meal').

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Null Message                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%Default Message in case of no advice triggered
msg('NULL', '').