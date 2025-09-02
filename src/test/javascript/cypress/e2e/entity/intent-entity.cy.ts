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

describe('IntentEntity e2e test', () => {
  const intentEntityPageUrl = '/intent-entity';
  const intentEntityPageUrlPattern = new RegExp('/intent-entity(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const intentEntitySample = { name: 'tensely rubbery psst' };

  let intentEntity;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/intent-entities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/intent-entities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/intent-entities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (intentEntity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/intent-entities/${intentEntity.id}`,
      }).then(() => {
        intentEntity = undefined;
      });
    }
  });

  it('IntentEntities menu should load IntentEntities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('intent-entity');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('IntentEntity').should('exist');
    cy.url().should('match', intentEntityPageUrlPattern);
  });

  describe('IntentEntity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(intentEntityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create IntentEntity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/intent-entity/new$'));
        cy.getEntityCreateUpdateHeading('IntentEntity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentEntityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/intent-entities',
          body: intentEntitySample,
        }).then(({ body }) => {
          intentEntity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/intent-entities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/intent-entities?page=0&size=20>; rel="last",<http://localhost/api/intent-entities?page=0&size=20>; rel="first"',
              },
              body: [intentEntity],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(intentEntityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details IntentEntity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('intentEntity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentEntityPageUrlPattern);
      });

      it('edit button click should load edit IntentEntity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IntentEntity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentEntityPageUrlPattern);
      });

      it('edit button click should load edit IntentEntity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('IntentEntity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentEntityPageUrlPattern);
      });

      it('last delete button click should delete instance of IntentEntity', () => {
        cy.intercept('GET', '/api/intent-entities/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('intentEntity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', intentEntityPageUrlPattern);

        intentEntity = undefined;
      });
    });
  });

  describe('new IntentEntity page', () => {
    beforeEach(() => {
      cy.visit(`${intentEntityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('IntentEntity');
    });

    it('should create an instance of IntentEntity', () => {
      cy.get(`[data-cy="name"]`).type('restfully awareness');
      cy.get(`[data-cy="name"]`).should('have.value', 'restfully awareness');

      cy.get(`[data-cy="optional"]`).should('not.be.checked');
      cy.get(`[data-cy="optional"]`).click();
      cy.get(`[data-cy="optional"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        intentEntity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', intentEntityPageUrlPattern);
    });
  });
});
