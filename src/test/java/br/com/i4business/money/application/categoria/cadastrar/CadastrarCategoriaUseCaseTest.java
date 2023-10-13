package br.com.i4business.money.application.categoria.cadastrar;

import br.com.i4business.money.application.UseCaseTest;
import br.com.i4business.money.domain.data.categoria.CategoriaGateway;
import br.com.i4business.money.domain.data.item.ItemGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class CadastrarCategoriaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCadastrarCategoriaUseCase useCase;

    @Mock
    private CategoriaGateway categoriaGateway;

    @Mock
    private ItemGateway itemGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoriaGateway, itemGateway);
    }

    // 1. Teste do caminho feliz
    // 2. Teste passando uma propriedade inválida (name)
    // 3. Teste criando uma categoria inativa
    // 4. Teste simulando um erro generico vindo do gateway

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand =
                CadastrarCategoriaCommand.com(expectedName, expectedDescription, expectedIsActive, new ArrayList<>());

        when(categoriaGateway.cadastrar(any()))
                .thenAnswer(returnsFirstArg());
        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        Mockito.verify(categoriaGateway, times(1)).cadastrar(argThat(aCategory ->
                Objects.equals(expectedName, aCategory.getNome())
                        && Objects.equals(expectedDescription, aCategory.getDescricao())
                        && Objects.equals(expectedIsActive, aCategory.isAtivo())
                        && Objects.nonNull(aCategory.getId())
                        && Objects.nonNull(aCategory.getCriadoEm())
                        && Objects.nonNull(aCategory.getAlteradoEm())
                        && Objects.isNull(aCategory.getDeletadoEm())
        ));
    }
    // Teste passando uma propriedade inválida (name)
    @Test
    public void givenAnInvalidCommand_whenCallsCreateCategory_shouldThrowException() {
        final var invalidName = ""; // Nome inválido

        final var aCommand =
                CadastrarCategoriaCommand.com(invalidName, "Descrição válida", true, new ArrayList<>());

        when(categoriaGateway.cadastrar(any()))
                .thenThrow(IllegalArgumentException.class);

        // Aqui verifico se uma exceção é lançada ao chamar o método execute com o comando inválido
        Assertions.assertThrows(IllegalArgumentException.class, () -> useCase.execute(aCommand));
    }
    // 3. Teste criando uma categoria inativa

    @Test
    public void givenAnInactiveCategory_whenCallsCreateCategory_shouldReturnCategoryId() {
        // Definindo os valores esperados para a categoria inativa
        final var expectedName = "Categoria Inativa";
        final var expectedDescription = "Uma categoria inativa";
        final var expectedIsActive = false; // Definindo a categoria como inativa

        // Criando o comando com os valores esperados
        final var aCommand = CadastrarCategoriaCommand.com(expectedName, expectedDescription, expectedIsActive, new ArrayList<>());

        // Configurando o comportamento do mock do CategoriaGateway
        when(categoriaGateway.cadastrar(any())).thenAnswer(returnsFirstArg());

        // Executando o caso de uso
        final var actualOutput = useCase.execute(aCommand);

        // Verificando se o resultado não é nulo e se possui um ID
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        // Verificando se o CategoriaGateway foi chamado com os valores esperados
        Mockito.verify(categoriaGateway, times(1)).cadastrar(argThat(aCategory ->
                Objects.equals(expectedName, aCategory.getNome())
                        && Objects.equals(expectedDescription, aCategory.getDescricao())
                        && Objects.equals(expectedIsActive, aCategory.isAtivo())
                        && Objects.nonNull(aCategory.getId())
                        && Objects.nonNull(aCategory.getCriadoEm())
                        && Objects.nonNull(aCategory.getAlteradoEm())
                        && Objects.isNull(aCategory.getDeletadoEm())
        ));
    }


}
