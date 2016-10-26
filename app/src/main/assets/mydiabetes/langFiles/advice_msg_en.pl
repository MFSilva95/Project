%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                           Advice Messages                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


msg('hadHypoGlycemia', ['You have a low Glycemia value.', 'You should eat 15 grams of carbohydrates and re-test your glycaemia values. \n\n A new glycaemia registry has already been scheduled.',['ALERT','alarmMessage','Glycemia',6,s],9]).
msg('hadLowGlucose',  ['You should eat to compensate the exercise done.', 'A new test will -be agended to check if your values have estabilized.',['SUGGESTION','InsulinDetail'],5]).
msg('hadRecentInsulin',  ['Congratulations! you ve been loosing weight.', 'A new test will -be agended to check if your values have estabilized.',['ALERT','alarmMessage','Glycemia',6,s],6 ]).

%%types are: NORMAL, SUGGESTION, QUESTION, ALERT};



%%In this file Messages are inserted or defined

msg('hasHighGlucose', 'You have a high Glycemia value. A new test will be agended to check if your values have estabilized./adviceTimer-Glycemia-600-s/').
msg('hasLowGlucose', 'You have a low Glycemia value. A new test will -be agended to check if your values have estabilized./adviceTimer-Glycemia-600-s/').

msg('hasHighHbA1c', 'You have a high HbA1c value. A new test will be agended to check if your values have estabilized./adviceTimer-HbA1c-600-s/').
msg('hasLowHbA1c', 'You have a low HbA1c value. A new test will be agended to check if your values have estabilized./adviceTimer-HbA1c-600-s/').

msg('hasHighCholesterol', 'You have a high Cholesterol value. A new test will be agended to check if your values have estabilized./adviceTimer-Cholesterol-600-s/').
msg('hasLowCholesterol', 'You have a low Cholesterol value. A new test will be agended to check if your values have estabilized./adviceTimer-Cholesterol-600-s/').
						 
msg('hasHighInsulin', 'The value of insulin inserted is above the normal value. A new test will be agended to check if this dose didnt afflict you./adviceTimer-Glycemia-600-s/').

msg('hasHighWeight', 'Your Weight is high. Please be careful with your weight./simpleAdvice/').
msg('hasLowWeight', 'Your Weight is low. Please be careful with your weight./simpleAdvice/').

msg('hasHighArterialPressure', 'You have a high Arterial Pressure value. A new test will be agended to check if this dose didnt afflict you./adviceTimer-BloodPressure-600-s/').
msg('hasLowArterialPressure', 'You have a low Arterial Pressure value. A new test will be agended to check if this dose didnt afflict you./adviceTimer-BloodPressure-600-s/').

msg('mealExercisedRecently', 'You have recently made physical exercise. Be aware that the suggested insulin value may not correspond to the real value due to physical exercise not being considerer in the insulin calculation./simpleAdvice/').
msg('mealUserWithHighWeight', 'You should moderate the portion of your meals and be careful with your weight./simpleAdvice/').

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