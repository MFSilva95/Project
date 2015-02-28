Se após importar o projeto der erro de compilação do java fazer: Android Tools -> Fix Project Propertires.

Colocar o projeto no eclipse ir à pasta libs e retirar o zip: "MyDiabetes - Libs externas (actionbarsherlock + holoeverywhere)"
Extrair o ficheiro zip (para uma pasta externa ao projecto).

Fazer import do actionbarsherlock para o eclipse.
Fazer import do holoeverywhere library para o eclipse.

Se todos os projetos ficarem sem erros (apenas com warnings) está pronto. Ver apenas a nota final. Caso contrário seguir o resto do tutorial.

1.
No holoeverywhere library ir a properties->Android->Remover a library-> Apply de seguida fazer Add dessa lib e Apply Novamente.
No MyDiabetes entrar em properties->Android->Remover a library-> Apply (fazer para as duas) de seguida fazer Add dessas libs e Apply Novamente.

Se todos os projetos ficarem sem erros (apenas com warnings) está pronto. Ver apenas a nota final. Caso contrário seguir o resto do tutorial.

2.
Nos 2 projetos das libs anteriormente importados aceder às propriedades do projeto -> Java Build Path->Sources
Apontar os nomes das fontes em cada um, remover as fontes e reiniciar o eclipse.
Ir novamente ao mesmo local e adicionar novamente as fontes removidas.

3.
Ir às propriedades do MyDiabetes propriedades do projeto -> Java Build Path -> Sources.
Apontar os nomes das fontes, remover as fontes e reiniciar o eclipse.
Ir novamente ao mesmo local e adicionar novamente as fontes removidas.

Por último Abrir o projeto MyDiabetes->libs e copiar o ficheiro android-support-v4.jar, colar este ficheiro no projeto actionbarsherlock->libs.

NOTA IMPORTANTE:
Caso apareça o seguinte erro ao correr o projeto: "unable to execute dex: gc overhead limit exceeded gc overhead limit exceeded"
Abrir o eclipse.ini e alterar os valores para do Xms e Xmx:
-Xms512m
-Xmx1024m