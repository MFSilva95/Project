:- load_foreign_files([myddas], [], init_myddas).
:- c_db_initialize_myddas.
:- module(myddas,[
    db_open/5,
    db_open/4,
    db_close/1,
    db_close/0,
    db_verbose/1,
    db_module/1,
    db_is_database_predicate/3,
   db_sql/2,
    db_sql/3,
    db_sql_select/3,
    db_prolog_select/2,
    db_prolog_select/3,
    db_prolog_select_multi/3,
    db_command/2,
    db_assert/2,
    db_assert/1,
    db_create_table/3,
    db_export_view/4,
    db_update/2,
    db_get_attributes_types/2,
    db_get_attributes_types/3,
    db_number_of_fields/2,
    db_number_of_fields/3,
    db_multi_queries_number/2,
    % myddas_top_level.ypp
    % myddas_assert_predicates.ypp
   db_import/2,
    db_import/3,
    db_view/2,
    db_view/3,
    db_insert/2,
    db_insert/3,
    db_abolish/2,
    db_listing/0,
    db_listing/1
   ]).
%% @}
%% @}
%% @}
%% @}
%% @}
%% @}
%% @}
%% @}
%%@}
%%@}
%% @}
:- use_module(myddas_assert_predicates,[
     db_import/2,
     db_import/3,
     db_view/2,
     db_view/3,
     db_insert/2,
     db_insert/3,
     db_abolish/2,
     db_listing/0,
     db_listing/1
           ]).
:- use_module(myddas_util_predicates,[
          '$prolog2sql'/3,
          '$create_multi_query'/3,
          '$get_multi_results'/4,
          '$process_sql_goal'/4,
          '$process_fields'/3,
          '$get_values_for_insert'/3,
          '$make_atom'/2,
          '$write_or_not'/1,
          '$abolish_all'/1,
          '$make_a_list'/2,
          '$get_table_name'/2,
          '$get_values_for_update'/4,
          '$extract_args'/4,
         '$lenght'/2
         ]).
:- use_module(myddas_errors,[
        '$error_checks'/1
       ]).
:- use_module(myddas_prolog2sql,[
     translate/3,
     queries_atom/2
    ]).
:- use_module(lists,[
       append/3
      ]).
:- use_module(library(parameters)).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_open/5
    % db_open/4
    %
:- db_open(Protocol) extra_arguments
db=Db,
port=Port,
socket=Socket,
user=User,
password=Password,
   % next arguments all refer to the data access point,
% so they are all are mutually exclusive
data:file=File,
data:host=Host/Db,
data:host=Host/Db/Port,
data:host=Host/Db/Socket,
data:odbc=ODBC_DSN
such_that
Connection?=myddas,
Host ?= localhost,
User ?= '',
Password ?= '',
t_atom(X) =:= atom(X) \/ t_var(X) \/ type_err(atom(X)),
t_var(X) =:= err(var(X)),
t_integer(X) =:= integer(X) \/ t_var(X) \/ err(integer(X)),
i_atom(X) =:= atom(X) \/ i_var(X) \/ t(atom(X)),
i_var(X) =:= i(var(X)),
i_integer(X) =:= integer(X) \/ i_var(X) \/ err(integer(X)),
( list( X ) =:= ( nil(X) + ( X = cons( A, Y ) * L ) ) ),
list( Protocol ) .
 %c_db_odbc_connect ==> i_atom( Password ) * i_atom( User ) *
(- (c^c_sqlite3_connect(File,User,Password,Handle)) :- (c ^fail) ).
db_open(Protocol) :- true.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_close/1
    % db_close/0
    %
db_close:-
 db_close(myddas).
db_close(Protocol):-
 '$error_checks'(db_close(Protocol)),
 get_value(Protocol,Con),
 '$abolish_all'(Con).
db_close(Protocol) :-
 '$error_checks'(db_close(Protocol)),
 get_value(Protocol,Con),
 c_db_connection_type(Con,ConType),
 ( ConType == mysql ->
   c_db_my_disconnect(Con)
 ;ConType == postgres ->
   c_postgres_disconnect(Con)
 ;ConType == sqlite3 ->
   c_sqlite3_disconnect(Con)
 ;
   c_db_odbc_disconnect(Con)
 ),
 set_value(Protocol,[]). % "deletes" atom
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_verbose/1
    %
    %
db_verbose(X):-
 var(X),!,
 get_value(db_verbose,X).
db_verbose(N):-!,
 set_value(db_verbose,N).
    %default value
:- set_value(db_verbose,0).
:- set_value(db_verbose_filename,myddas_queries).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_module/1
    %
    %
db_module(X):-
 var(X),!,
 get_value(db_module,X).
db_module(ModuleName):-
 set_value(db_module,ModuleName).
    % default value
:- db_module(user).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_is_database_predicate(+,+,+)
    %
    %
db_is_database_predicate(Module,PredName,Arity):-
 '$error_checks'(db_is_database_predicate(PredName,Arity,Module)),
 c_db_check_if_exists_pred(PredName,Arity,Module).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_sql(+,+,-)
    %
    %
    %compatibility
db_sql_select(Protocol,SQL,LA):-
 db_sql(Protocol,SQL,LA).
db_sql(SQL,LA):-
 db_sql(myddas,SQL,LA).
db_sql(Connection,SQL,LA):-
 '$error_checks'(db_sql(Connection,SQL,LA)),
 get_value(Connection,Con),
 c_db_connection_type(Con,ConType),
 '$write_or_not'(SQL),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(SQL,ResultSet,Con,Mode,Arity)
 ;ConType == postgres ->
   postgres_result_set(Mode),
   c_postgres_query(SQL,ResultSet,Con,Mode,Arity)
 ;ConType == sqlite3 ->
   sqlite3_result_set(Mode),
   c_sqlite3_query(SQL,ResultSet,Con,Mode,Arity)
 ;
   c_db_odbc_number_of_fields_in_query(SQL,Con,Arity)
 ),
 '$make_a_list'(Arity,LA),
 ( ConType == mysql ->
   !,c_db_my_row(ResultSet,Arity,LA)
 ;
   '$make_a_list'(Arity,BindList),
   c_db_odbc_query(SQL,ResultSet,Arity,BindList,Con),!,
   c_db_odbc_row(ResultSet,BindList,LA)
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_prolog_select(+,+,+)
    %
    %
db_prolog_select(LA,DbGoal):-
 db_prolog_select(myddas,LA,DbGoal).
db_prolog_select(Connection,LA,DbGoal):-
 '$lenght'(LA,Arity),
 Name=viewname,
 functor(ViewName,Name,Arity),
    % build arg list for viewname/Arity
 ViewName=..[Name|LA],
 '$prolog2sql'(ViewName,DbGoal,SQL),
 get_value(Connection,Con),
 c_db_connection_type(Con,ConType),
 '$write_or_not'(SQL),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(SQL,ResultSet,Con,Mode,_),
   !,c_db_my_row(ResultSet,Arity,LA)
 ;
   true
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_prolog_select_multi(+,+,-)
    % db_prolog_select_multi(guest,[(ramos(A,C),A=C),(ramos(D,B),B=10)],[[A],[D,B]]).
    %
db_prolog_select_multi(Connection,DbGoalsList,ListOfResults) :-
 '$error_checks'(db_prolog_select_multi(Connection,DbGoalsList,ListOfResults)),
 '$create_multi_query'(ListOfResults,DbGoalsList,SQL),
 get_value(Connection,Con),
 c_db_connection_type(Con,ConType),
 '$write_or_not'(SQL),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(SQL,ResultSet,Con,Mode,_)
 ;
   true
 ),
 '$get_multi_results'(Con,ConType,ResultSet,ListOfResults).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_command/2
    %
    %
db_command(Connection,SQL):-
 '$error_checks'(db_command(Connection,SQL)),
 get_value(Connection,Con),
 '$write_or_not'(SQL),
 c_db_connection_type(Con,ConType),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(SQL,_,Con,Mode,_)
 ;
   true
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_assert/2
    % db_assert/1
    %
db_assert(PredName):-
 db_assert(myddas,PredName).
db_assert(Connection,PredName):-
 translate(PredName,PredName,Code),
 '$error_checks'(db_insert2(Connection,PredName,Code)),
 '$get_values_for_insert'(Code,ValuesList,RelName),
 '$make_atom'(['INSERT INTO `',RelName,'` VALUES '|ValuesList],SQL),
 get_value(Connection,Con),
 c_db_connection_type(Con,ConType),
 '$write_or_not'(SQL),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(SQL,_,Con,Mode,_)
 ;ConType == postgres ->
   postgres_result_set(Mode),
   c_postgres_query(SQL,_,Con,Mode,_)
 ;ConType == sqlite3 ->
   sqlite3_result_set(Mode),
   c_sqlite3_query(SQL,_,Con,Mode,_)
 ;
   c_db_odbc_query(SQL,_,_,_,Con)
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_create_table/3
    % FieldsList = [field(Name,Type,Null,Key,DefaultValue)]
    % Example [field(campo1,'char(12)',y,y,a),field(campo2,int,y,y,0)]
    % TODO Test with ODBC & Type Checks
db_create_table(Connection,TableName,FieldsInf):-
 '$error_checks'(db_create_table(Connection,TableName,FieldsInf)),
 get_value(Connection,Con),
 '$process_fields'(FieldsInf,FieldString,KeysSQL),
 '$make_atom'(['CREATE TABLE `',TableName,'` ( ',FieldString,KeysSQL,' )'],FinalSQL),
 c_db_connection_type(Con,ConType),
 '$write_or_not'(FinalSQL),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(FinalSQL,_,Con,Mode,_)
 ;ConType == posgres ->
   postgres_result_set(Mode),
   c_postsgres_query(FinalSQL,_,Con,Mode,_)
 ;ConType == sqlite3 ->
   sqlite3_result_set(Mode),
   c_sqlite3_query(FinalSQL,_,Con,Mode,_)
 ;
   c_db_odbc_query(FinalSQL,_,_,_,Con)
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_export_view/4
    % TODO Test with ODBC
    %
db_export_view(Connection,TableViewName,SQLorDbGoal,FieldsInf):-
 '$error_checks'(db_export_view(Connection,TableViewName,SQLorDbGoal,FieldsInf)),
 get_value(Connection,Con),
 '$process_sql_goal'(TableViewName,SQLorDbGoal,TableName,SQL),
    % Case there's some information about the
    % attribute fields of the relation given
    % by the user
 ( FieldsInf == [] ->
   '$make_atom'(['CREATE TABLE ',TableName,' AS ',SQL],FinalSQL)
 ;
   '$process_fields'(FieldsInf,FieldString,KeysSQL),
   '$make_atom'(['CREATE TABLE ',TableName,' (',FieldString,KeysSQL,') AS ',SQL],FinalSQL)
 ),
 c_db_connection_type(Con,ConType),
 '$write_or_not'(FinalSQL),
 ( ConType == mysql ->
   db_my_result_set(Mode),
   c_db_my_query(FinalSQL,_,Con,Mode,_)
 ;
   c_db_odbc_query(FinalSQL,_,_,_,Con)
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_update/2
    % BUG: db_update dosen't work for this case, just an example
    % db_update(my1,edge(1,3)-edge(99,99)).
    % The case where the set condition is "set" to change all the fields
db_update(Connection,WherePred-SetPred):-
    %TODO: error_checks
 get_value(Connection,Conn),
    % Match and Values must be "unifiable"
 functor(WherePred,PredName,Arity),
 functor(SetPred,PredName,Arity),
 functor(NewRelation,PredName,Arity),
 '$extract_args'(WherePred,1,Arity,WhereArgs),
 '$extract_args'(SetPred,1,Arity,SetArgs),
 copy_term(WhereArgs,WhereArgsTemp),
 NewRelation=..[PredName|WhereArgsTemp],
 translate(NewRelation,NewRelation,Code),
 '$get_values_for_update'(Code,SetArgs,SetCondition,WhereCondition),
 '$get_table_name'(Code,TableName),
 append(SetCondition,WhereCondition,Conditions),
 '$make_atom'(['UPDATE `',TableName,'` '|Conditions],SQL),
 '$write_or_not'(SQL),
 db_my_result_set(Mode),
 c_db_my_query(SQL,_,Conn,Mode,_).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_get_attributes_types/3
    % db_get_attributes_types/2
    %
db_get_attributes_types(RelationName,TypesList) :-
 db_get_attributes_types(myddas,RelationName,TypesList).
db_get_attributes_types(Connection,RelationName,TypesList) :-
 '$error_checks'(db_get_attributes_types(Connection,RelationName,TypesList)),
 get_value(Connection,Con),
 c_db_connection_type(Con,ConType),
 ( ConType == mysql ->
   c_db_my_number_of_fields(RelationName,Con,Arity)
 ;
   c_db_odbc_number_of_fields(RelationName,Con,Arity)
 ),
 Size is 2*Arity,
 '$make_a_list'(Size,TypesList),
 c_db_connection_type(Con,ConType),
 ( ConType == mysql ->
   c_db_my_get_attributes_types(RelationName,Con,TypesList)
 ;
   c_db_odbc_get_attributes_types(RelationName,Con,TypesList)
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_number_of_fields/3
    %
    %
db_number_of_fields(RelationName,Arity) :-
 db_number_of_fields(myddas,RelationName,Arity).
db_number_of_fields(Connection,RelationName,Arity) :-
 '$error_checks'(db_number_of_fields(Connection,RelationName,Arity)),
 get_value(Connection,Con),
 c_db_connection_type(Con,ConType),
 ( ConType == mysql ->
   c_db_my_number_of_fields(RelationName,Con,Arity)
 ;ConType == postgres ->
   c_postgres_number_of_fields(RelationName,Con,Arity)
 ;ConType == sqlite3 ->
   c_sqlite3_number_of_fields(RelationName,Con,Arity)
 ;
   c_db_odbc_number_of_fields(RelationName,Con,Arity)
 ).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % db_multi_queries_number(+,+)
    % TODO: EVERITHING
    %
db_multi_queries_number(Connection,Number) :-
 '$error_checks'(db_multi_queries_number(Connection,Number)),
 get_value(Connection,Con),
 c_db_multi_queries_number(Con,Number).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
