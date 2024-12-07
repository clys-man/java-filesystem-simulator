## README - Simulador de Sistema de Arquivos em Java com Journaling

### **Descrição do Projeto**
Este projeto consiste no desenvolvimento de um simulador de sistema de arquivos para auxiliar na compreensão do funcionamento básico de um sistema de arquivos em um sistema operacional. Implementamos funcionalidades essenciais como criação, remoção, renomeação e listagem de arquivos e diretórios, além de um sistema de Journaling para garantir a integridade das operações realizadas.

---

### **Funcionalidades Implementadas**
- Criação de diretórios e arquivos.
- Exclusão de arquivos e diretórios.
- Renomeação de arquivos e diretórios.
- Listagem do conteúdo de diretórios.
- Escrita em arquivos.
- Sistema de Journaling para recuperação e rastreamento de operações.

---

### **Tecnologias Utilizadas**
- **Linguagem de programação:** Java
- **Estruturas de dados:** Classes Java (`Directory`, `FileNode`) para modelar arquivos e diretórios.
- **Persistência:** Arquivo serializado para simular o armazenamento do sistema de arquivos.
- **Journaling:** Logs de operações em um arquivo `journal.log` para garantir integridade em caso de falhas.

---

### **Arquitetura do Simulador**

#### **1. Estrutura de Dados**
O simulador utiliza uma **estrutura de dados em árvore** para representar a hierarquia de diretórios e arquivos.

- Cada diretório é representado pela classe `Directory`, que contém:
    - Referência para seu diretório pai.
    - Um mapa de subdiretórios (`subdirectories`).
    - Um mapa de arquivos (`files`).

Essa abordagem reflete a organização hierárquica dos sistemas de arquivos reais, permitindo navegar, adicionar e remover elementos de forma eficiente.

Exemplo de representação:

```  
/  
├── documentos  
│   ├── projetos  
│   └── notas.txt  
└── imagens  
    ├── foto1.jpg  
    └── foto2.png  
```  

No código:
```java  
Directory root = new Directory("/");  
Directory documentos = new Directory("documentos", root);  
root.addSubdirectory("documentos");  
documentos.addFile("notas.txt");  
```  

A busca por arquivos ou diretórios é feita navegando na estrutura de árvore, iniciando na raiz (`root`) e percorrendo os nós conforme necessário.

#### **2. Journaling**
O sistema utiliza um arquivo de log (`journal.log`) para registrar operações:
- **PENDING**: Indica que uma operação foi iniciada, mas não concluída.
- **COMMITTED**: Indica que a operação foi concluída com sucesso.

Exemplo de registro no log:
```
PENDING MKDIR /docs  
COMMITTED MKDIR /docs  
```  

#### **3. Modo Shell**
O simulador funciona como um Shell em que o usuário pode executar comandos interativos.

Exemplo de uso no Shell:
```
> mkdir docs  
Diretório criado com sucesso.  
> touch docs/notes.txt  
Arquivo criado com sucesso.  
> write "Hello World" docs/notes.txt  
Conteúdo gravado no arquivo.  
> cat docs/notes.txt  
Hello World  
```

---

### **Implementação**

#### **Classe Main**
A classe principal gerencia o sistema de arquivos e processa os comandos do usuário.

Exemplo:
```java  
switch (command) {  
    case "mkdir" -> makeDirectory(argument);  
    case "touch" -> createFile(argument);  
    case "ls" -> listDirectory();  
    default -> System.out.println("Comando não reconhecido.");  
}  
```  

#### **Classe Directory**
A classe `Directory` armazena subdiretórios e arquivos, implementando a estrutura em árvore.

Exemplo:
```java  
boolean addSubdirectory(String name) {  
    if (subdirectories.containsKey(name)) return false;  
    subdirectories.put(name, new Directory(name, this));  
    return true;  
}  
```  

#### **Classe FileNode**
A classe `FileNode` representa arquivos e permite operações como escrita e leitura.

Exemplo:
```java  
void appendContent(String newContent) {  
    content.append(newContent).append(System.lineSeparator());  
}  
```  

---

### **Como Executar**

#### **Pré-requisitos**
- JDK 21 ou superior.

#### **Passos para Execução**
1. Clone o repositório:
   ```bash  
   git clone <link_do_repositorio>  
   cd simulador-sistema-de-arquivos  
   ```  
2. Compile e execute o programa:
   ```bash  
   javac -d bin src/edu/clysman/unifor/filesystem/*.java  
   java -cp bin edu.clysman.unifor.filesystem.Main  
   ```  

---

### **Exemplo de Uso**

#### **1. Criar e navegar em diretórios**
```  
> mkdir documentos  
Diretório criado com sucesso.  
> cd documentos  
> mkdir projetos  
Diretório criado com sucesso.  
> ls  
[D] projetos  
```  

#### **2. Criar e manipular arquivos**
```  
> touch notas.txt  
Arquivo criado com sucesso.  
> write "Simulador de Sistema de Arquivos" notas.txt  
Conteúdo gravado no arquivo.  
> cat notas.txt  
Simulador de Sistema de Arquivos  
```  

#### **3. Logs de Operações (Journaling)**
```  
> log  
COMMITTED MKDIR /documentos  
COMMITTED TOUCH /documentos/notas.txt  
COMMITTED WRITE /documentos/notas.txt Simulador de Sistema de Arquivos  
```  

---

### **Conclusão**
Este simulador implementa os principais conceitos de um sistema de arquivos com journaling, utilizando uma estrutura de dados em árvore para gerenciar a hierarquia de diretórios e arquivos. Ele fornece uma visão prática e interativa de como os sistemas operacionais gerenciam arquivos e diretórios, além de garantir a integridade dos dados por meio de registros de logs.

---

### **Autores**
- Clysman Alves
- Pedro Henrique do Nascimento Lins