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

describe('IntentResponse e2e test', () => {
  const intentResponsePageUrl = '/intent-response';
  const intentResponsePageUrlPattern = new RegExp('/intent-response(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const intentResponseSample = { message: 'excluding gee' };

  let intentResponse;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/intent-responses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/intent-responses').as('postEntityRequest');
    cy.intercept('DELETE', '/api/intent-responses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (intentResponse) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/intent-responses/${intentResponse.id}`,
      }).then(() => {
        intentResponse = undefined;
      });
    }
  });

  it('IntentResponses menu should load IntentResponses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('intent-response');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('IntentResponse').should('exist');
    cy.url().should('match', intentResponsePageUrlPattern);
  });

  describe('IntentResponse page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(intentResponsePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create IntentResponse page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/intent-response/new$'));
        cy.getEntityCreateUpdateHeading('IntentResponse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentResponsePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/intent-responses',
          body: intentResponseSample,
        }).then(({ body }) => {
          intentResponse = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/intent-responses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [intentResponse],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(intentResponsePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details IntentResponse page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('intentResponse');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentResponsePageUrlPattern);
      });

      it('edit button click should load edit IntentResponse page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IntentResponse');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentResponsePageUrlPattern);
      });

      it('edit button click should load edit IntentResponse page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IntentResponse');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentResponsePageUrlPattern);
      });

      it('last delete button click should delete instance of IntentResponse', () => {
        cy.intercept('GET', '/api/intent-responses/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('intentResponse').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentResponsePageUrlPattern);

        intentResponse = undefined;
      });
    });
  });

  describe('new IntentResponse page', () => {
    beforeEach(() => {
      cy.visit(`${intentResponsePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('IntentResponse');
    });

    it('should create an instance of IntentResponse', () => {
      cy.get(`[data-cy="message"]`).type('bulky general clueless');
      cy.get(`[data-cy="message"]`).should('have.value', 'bulky general clueless');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        intentResponse = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', intentResponsePageUrlPattern);
    });
  });
});
