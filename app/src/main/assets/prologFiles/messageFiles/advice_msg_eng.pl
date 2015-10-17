%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                           Advice Messages                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%In this file Messages are inserted or defined
% messages must have the format msg(ID, Message with extra options separated by "-")

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

msg('Tsk_glycemiaReg', 'You dont test your Glucose for far too long. The last registrated value has expired. Please perform a new test./10').
msg('Tsk_hbA1cReg', 'You dont test your HbA1c for far too long. The last registrated value has expired. Please perform a new test./10').
msg('Tsk_arterialPReg', 'You dont test your Arterial Pressure for far too long. The last registrated value has expired. Please perform a new test./10').
msg('Tsk_weightReg', 'You dont weight yourself for far too long. The last registrated value has expired. Please perform a new weighing./10').
msg('Tsk_cholesterolReg', 'You dont test your Cholesterol for far too long. The last registrated value has expired. Please perform a new test./10').

%%------------------------------------------------------------------------------------------------------------
%% Other Task messages
%%------------------------------------------------------------------------------------------------------------

msg('Tsk_doExerciseTwoTimesADay', 'You must Exercise at least two times a day and register it.').

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