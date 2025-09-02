import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Utterance e2e test', () => {
  const utterancePageUrl = '/utterance';
  const utterancePageUrlPattern = new RegExp('/utterance(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const utteranceSample = { text: 'sure-footed' };

  let utterance;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/utterances+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/utterances').as('postEntityRequest');
    cy.intercept('DELETE', '/api/utterances/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (utterance) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/utterances/${utterance.id}`,
      }).then(() => {
        utterance = undefined;
      });
    }
  });

  it('Utterances menu should load Utterances page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('utterance');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Utterance').should('exist');
    cy.url().should('match', utterancePageUrlPattern);
  });

  describe('Utterance page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(utterancePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Utterance page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/utterance/new$'));
        cy.getEntityCreateUpdateHeading('Utterance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', utterancePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/utterances',
          body: utteranceSample,
        }).then(({ body }) => {
          utterance = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/utterances+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [utterance],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(utterancePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Utterance page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('utterance');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', utterancePageUrlPattern);
      });

      it('edit button click should load edit Utterance page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Utterance');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', utterancePageUrlPattern);
      });

      it('edit button click should load edit Utterance page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Utterance');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', utterancePageUrlPattern);
      });

      it('last delete button click should delete instance of Utterance', () => {
        cy.intercept('GET', '/api/utterances/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('utterance').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', utterancePageUrlPattern);

        utterance = undefined;
      });
    });
  });

  describe('new Utterance page', () => {
    beforeEach(() => {
      cy.visit(`${utterancePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Utterance');
    });

    it('should create an instance of Utterance', () => {
      cy.get(`[data-cy="text"]`).type('bah phew');
      cy.get(`[data-cy="text"]`).should('have.value', 'bah phew');

      cy.get(`[data-cy="language"]`).type('because illiterate how');
      cy.get(`[data-cy="language"]`).should('have.value', 'because illiterate how');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        utterance = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', utterancePageUrlPattern);
    });
  });
});
