/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.imaging.chip.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ddf.catalog.content.data.ContentItem;
import ddf.catalog.data.Attribute;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.MetacardImpl;
import ddf.catalog.operation.ResourceRequest;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.Test;

public class CatalogInputAdapterTest {

  public static final String OVERVIEW = "overview";

  private CatalogInputAdapter catalogInputAdapter;

  private Attribute mockAttribute;

  private Metacard mockMetacard;

  @Before
  public void setUp() {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn(Metacard.RESOURCE_URI);
    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute(anyString())).thenReturn(attribute);
    this.mockMetacard = metacard;
    this.mockAttribute = attribute;
    this.catalogInputAdapter = new CatalogInputAdapter();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullMetacard() throws URISyntaxException {
    catalogInputAdapter.buildReadRequest(null, OVERVIEW);
  }

  @Test(expected = IllegalStateException.class)
  public void testNullAttribute() throws URISyntaxException {
    when(mockMetacard.getAttribute(anyString())).thenReturn(null);
    catalogInputAdapter.buildReadRequest(mockMetacard, OVERVIEW);
  }

  @Test(expected = IllegalStateException.class)
  public void testNullAttributeValue() throws URISyntaxException {
    when(mockAttribute.getValues()).thenReturn(null);
    when(mockMetacard.getAttribute(anyString())).thenReturn(mockAttribute);
    catalogInputAdapter.buildReadRequest(mockMetacard, OVERVIEW);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedURI() throws URISyntaxException {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn(Metacard.RESOURCE_URI);
    when(attribute.getValue()).thenReturn("   ");

    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute(anyString())).thenReturn(attribute);

    catalogInputAdapter.buildReadRequest(metacard, OVERVIEW);
  }

  @Test
  public void testSuccessfulCase() throws URISyntaxException {
    Attribute attribute = mock(Attribute.class);
    when(attribute.getName()).thenReturn(Metacard.RESOURCE_URI);
    when(attribute.getValue()).thenReturn("content:10101#overview");
    Metacard metacard = mock(Metacard.class);
    when(metacard.getAttribute(anyString())).thenReturn(attribute);

    ResourceRequest request = catalogInputAdapter.buildReadRequest(metacard, OVERVIEW);
    assertThat(request.getAttributeName(), is(Metacard.RESOURCE_URI));
    assertThat(request.getAttributeValue().toString(), is("content:10101#overview"));
    assertThat(request.getProperties().get(ContentItem.QUALIFIER_KEYWORD), is(OVERVIEW));
  }

  @Test
  public void testBuildRequest() {
    String id = "abcd";
    MetacardImpl metacard = new MetacardImpl();
    metacard.setId(id);
    ResourceRequest resourceRequest = catalogInputAdapter.buildReadRequest(metacard);
    assertThat(resourceRequest.getAttributeValue(), is(id));
  }

  @Test
  public void testGetResourceSiteName() {
    final String siteName = "siteNameValue";
    MetacardImpl metacard = new MetacardImpl();
    metacard.setSourceId(siteName);

    String resourceSiteName = catalogInputAdapter.getResourceSiteName(metacard);
    assertThat(resourceSiteName, is(siteName));
  }
}
