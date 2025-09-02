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

describe('Followup e2e test', () => {
  const followupPageUrl = '/followup';
  const followupPageUrlPattern = new RegExp('/followup(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const followupSample = { question: 'anti', targetEntity: 'while even zowie' };

  let followup;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/followups+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/followups').as('postEntityRequest');
    cy.intercept('DELETE', '/api/followups/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (followup) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/followups/${followup.id}`,
      }).then(() => {
        followup = undefined;
      });
    }
  });

  it('Followups menu should load Followups page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('followup');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Followup').should('exist');
    cy.url().should('match', followupPageUrlPattern);
  });

  describe('Followup page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(followupPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Followup page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/followup/new$'));
        cy.getEntityCreateUpdateHeading('Followup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', followupPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/followups',
          body: followupSample,
        }).then(({ body }) => {
          followup = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/followups+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [followup],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(followupPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Followup page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('followup');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', followupPageUrlPattern);
      });

      it('edit button click should load edit Followup page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Followup');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', followupPageUrlPattern);
      });

      it('edit button click should load edit Followup page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Followup');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', followupPageUrlPattern);
      });

      it('last delete button click should delete instance of Followup', () => {
        cy.intercept('GET', '/api/followups/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('followup').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', followupPageUrlPattern);

        followup = undefined;
      });
    });
  });

  describe('new Followup page', () => {
    beforeEach(() => {
      cy.visit(`${followupPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Followup');
    });

    it('should create an instance of Followup', () => {
      cy.get(`[data-cy="question"]`).type('lumpy immaculate uncork');
      cy.get(`[data-cy="question"]`).should('have.value', 'lumpy immaculate uncork');

      cy.get(`[data-cy="targetEntity"]`).type('save');
      cy.get(`[data-cy="targetEntity"]`).should('have.value', 'save');

      cy.get(`[data-cy="order"]`).type('9296');
      cy.get(`[data-cy="order"]`).should('have.value', '9296');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        followup = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', followupPageUrlPattern);
    });
  });
});
