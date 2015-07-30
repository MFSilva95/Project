%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                            Global Values                                                   %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% Define global values
%% Time is always defined in seconds
%%------------------------------------------------------------------------------------------------------------

maxTimeUntested(glycemia, 10800). %3h
maxTimeUntested(artecialPressure, 86400). %24h
maxTimeUntested(weight, 2592000). %1 mes
maxTimeUntested(hbA1c, 86400). %24h

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                          Subtype Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% To define a type subdivision a fact must be inserted
%% this fact must be of form adviceTypes(TypeToDivide, ListOfTypeDivisions)
%%------------------------------------------------------------------------------------------------------------

adviceTypes(glicose, [glicose,insulin,hbA1c]).
adviceTypes(exercicio, [food,insulin,exercise]).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Risk Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%% To define a test of risk a fact must be inserted
%% this fact must be of form inRisk(Type, SubType, Risk, ID) :- 
%% NOT bloqued(glicose),
%% (conditions to activate separeted by ','), msg(pt, ID, _). 
%% The facts must be inserted in decrescent order of Risk
%% The facts must be inserted from the most specific to the more abstracts
%%------------------------------------------------------------------------------------------------------------

inRisk( glicose, insulin, 5, ID) :- ID = hasHighGlycemia, NOT bloqued(ID), hasGlicemiaAlta, msg(SysLang, ID, _).
inRisk( glicose, _, 5, ID) :- NOT bloqued(glicose), hasGlicemiaAlta, msg(SysLang, ID, _).
inRisk( glicose, _, 3, ID) :- NOT bloqued(glicose), hasGlicemiaAlta, msg(SysLang, ID, _).



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Task Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%------------------------------------------------------------------------------------------------------------
%%
%%------------------------------------------------------------------------------------------------------------

hasTask(Description) :- timeOfLastGlucoseRegistry(LastRegTime), maxTimeUntested(glycemia, MaxTime), LastRegTime > MaxTime - 600, msg('Tsk1', Description).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                                                            %
%                                             Test Division                                                  %
%                                                                                                            %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

hasGlicemiaAlta :- lastGlucoseValue(Value), Value >= 120.


%-------------------------------------Regras Carla----------------------------


%-------Hipoglicemia

hipoglicemia(ValorBGRecente,HorasDepoisHipo) :- valorBGRecente(ValorBGRecente), valorMinBG(MinBG), tempoValidoHipo(HorasDepoisHipo),ValorBGRecente=<MinBG.

valoresBaixosPorExercicio(ValorBGRcente, HorasDepoisHipo) :- exercicioRecente(HorasDepoisEx), hipoglicemia(ValorBGRecente, HorasDepoisHipo).

valoresBaixosListaHC(ValorBGRecente, Min) :- valorBGRecente(ValorBGRecente), valorMinBG(MinBG), ValorBGRecente =< MinBG, minPassados(Min).

valoresBaixosPosMin(ValorBGRecente, Max) :- valorBGRecente(ValorBGRecente), valorMaxBG(MaxBG), ValorBGRecente >= MinBG, maxPassados(Max).

%-------Hiperglicemia

hiperglicemia(MaxBG, HoraUltimaRefeicao, HoraUltimaInsulina) :- hiperglicemia(ValorBGRecente, HoraDepoisHiper), refeicao(HoraUltimaRefeicao), naoInsulina(HoraUltimaInsulina).

hiperglicemia(ValorBGRecente, HorasDepoisHiper) :- valorBGRecente(ValorBGRecente), valorMaxBG(MaxBG), tempoValidoHiper(HorasDepoisHiper), ValorBGRecente >= MaxBG.

hiperglicemiacomCetoacidoseModerada(ValorBGRecente, ValorCetose) :- hiperglicemia(ValorBGRecente, HoraDepoisHiper), cetoacidoseModerada(ValorCetose).

hiperglicemiacomCetoasidadeGrave(ValorBGRecente, ValorCetose) :- hiperglicemia(ValorBGRecente, HoraDepoisHiper), cetoacidoseGrave(ValorCetose).

%-------Cetoacidose

cetoacidoseModerada(ValorCetose) :- valorCetoseRecente(ValorCetose), valorCetoseMin(MinCetose), ValorCetose >= 0.3, ValorCetose < 3.0.

cetoacidoseGrave(ValorCetose) :- valorCetoseRecente(ValorCetose), valorCetoseMan(MaxCetose), ValorCetose >= 3.0.

%-------Ajuste Insulina

ajustarInsulinaPorqueHipo(HorasDepoisHipo) :- hipoglicemia(ValorRecente, HorasDepoisHipo).

ajustarInsulinaPorqueExercicioRecente(HorasDepoisEx) :- exercicioRecente(HorasDepoisEx).

ajustarInsulinaPorqueNaoRefeicao(HoraUltimaRefeicao) :- naoRefeicao(HoraUltimaRefeicao).

%-------Idade Utilizador

utilizadorAdolescente() :- idadeUtilizador(Idade). 
Idade =< 18.

%------- Regra de Exercicio

exercicioRecente(HorasDepoisEx) :- tempoValidoExercicio(HorasDepoisEx).

